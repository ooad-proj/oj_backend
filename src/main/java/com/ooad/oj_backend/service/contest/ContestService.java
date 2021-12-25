package com.ooad.oj_backend.service.contest;

import cn.dev33.satoken.stp.StpUtil;

import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ContestMapper;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mapper.record.RecordMapper;
import com.ooad.oj_backend.mapper.user.AuthMapper;
import com.ooad.oj_backend.mapper.user.UserMapper;
import com.ooad.oj_backend.mybatis.entity.*;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ContestService {
    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private RecordMapper recordMapper;

    public ResponseEntity<?> getContestInformation(int contestId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            int classId=contestMapper.getClassByContest(contestId);
            ResponseEntity responseEntity1 = authService.checkPermission("0-" + classId);
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
            if (responseEntity2 !=null&&responseEntity1!=null){
                return responseEntity2;
            }
            if(responseEntity1==null){
                int s=state(contestId);
                if(s==-1){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        Response response=new Response();
        Contest contest = contestMapper.getOneContest(contestId);
        User creator=userMapper.getOne(contest.getCreatorId());
        if(creator!=null)
        contest.setCreatorName(creator.getName());
        List<Problem> problems = problemMapper.getContestProblem(contestId);
        User user=userMapper.getOne((String) StpUtil.getLoginId());
        List<UserResult>userResults= recordMapper.getContestResultByName(contestId,user.getId());
        //TODO: get myScore and score
        for (Problem problem:problems){
            for(UserResult userResult:userResults) {
                if(userResult.getShownId().equals(problem.getShownId()))
                problem.setMyScore(userResult.getScore());
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("contest",contest);
        map.put("problems",problems);
        response.setContent(map);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<?> getManagingContests(int page, int itemsPerPage,String search) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        Response response = new Response();
        int total;
        List<Contest> contests;
        if(responseEntity1==null){ //get for teacher
             total = contestMapper.getTotalNum(search);
            contests = contestMapper.getAllContest((page - 1) * itemsPerPage,itemsPerPage,search);
        }
        else {
            //get for assistant
            String userId = (String) StpUtil.getLoginId();
            contests = contestMapper.getManagementContest(userId,search);
            total = contestMapper.getManagementNumber(userId,search);
        }

        if(contests==null) {
            Paper paper = new Paper();
            paper.setItemsPerPage(0);
            paper.setPage(1);
            paper.setTotalAmount(0);
            paper.setTotalPage(1);
            paper.setList(new ArrayList());
            response.setContent(paper);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            Paper paper = new Paper();
            paper.setPage(page);
            paper.setTotalPage((total / itemsPerPage) + (((total % itemsPerPage) == 0) ? 0 : 1));
            paper.setItemsPerPage(itemsPerPage);
            paper.setTotalAmount(total);
            paper.setList(contests);
            response.setContent(paper);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    public ResponseEntity<?> addContest(int groupId , String title, String description, long startTime, long endTime,boolean access) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        if(responseEntity1 != null){
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
            if (responseEntity2 !=null){
                return responseEntity2;
            }
        }

        Response response = new Response();
        Contest contest =new Contest();
        contest.setId(0);
        contest.setAccess(access);
        contest.setClassId(groupId);
        contest.setTitle(title);
        contest.setDescription(description);
        contest.setStartTime(startTime);
        contest.setEndTime(endTime);
        contest.setCreatorId((String) StpUtil.getLoginId());
        contest.setCreatorName( userMapper.getOne((String) StpUtil.getLoginId()).getName());
        contestMapper.insert(contest);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> modifyContest(int contestId, String title, String description, long startTime, long endTime,boolean access) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        if (responseEntity1 != null) {
            Boolean flag = false;
            List<Contest> contests = contestMapper.getManagementContest((String) StpUtil.getLoginId(), "");
            for (int i = 0; i < contests.size(); i++) {
                if (contests.get(i).getId() == contestId) {
                    flag = true;
                    break;
                }
            }
            if (!flag){
                return responseEntity1;
            }
        }
        Response response = new Response();
        Contest contest = new Contest();
        contest.setId(contestId);
        contest.setAccess(access);
        contest.setTitle( title);
        contest.setDescription(description);
        contest.setStartTime(startTime);
        contest.setEndTime(endTime);
        contestMapper.update(contest);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteContest(int contestId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        if (responseEntity1 != null) {
            Boolean flag = false;
            List<Contest> contests = contestMapper.getManagementContest((String) StpUtil.getLoginId(), "");
            for (int i = 0; i < contests.size(); i++) {
                if (contests.get(i).getId() == contestId) {
                    flag = true;
                    break;
                }
            }
            if (!flag){
                return responseEntity1;
            }
        }
        Response response = new Response();
        Contest contest = contestMapper.getOneContest(contestId);
        if(contest==null){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        contestMapper.delete(contestId);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    int state(int contestId){
        Contest contest=contestMapper.getOneContest(contestId);
        long start=contest.getStartTime();
        long end=contest.getEndTime();
        long time=System.currentTimeMillis();
        if(time<=start) {
            return -1;
        }if(time<end)
            return 0;
        return 1;
    }

    public ResponseEntity<?> getCloseContest(int amount) {
        Response response = new Response();
        String userId;
        if(StpUtil.isLogin()) {
            userId = (String) StpUtil.getLoginId();
        }else {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        long nowTime = System.currentTimeMillis();
        List<Contest> contests = contestMapper.getCloseContest(nowTime);
        List<Contest> sortedContest = contests.stream().sorted(Comparator.comparing( Contest::getEndTime)).limit(amount).collect(Collectors.toList());
        List<Content> contents = new ArrayList<>();
        List<Contest> allAllowedContest = contestMapper.getAllowedContest(userId);
        for(int i =0;i<sortedContest.size();i++){
            Content tem = new Content();
            tem.contestId = contests.get(i).getId();
            tem.title = contests.get(i).getTitle();
            tem.timeLeft =contests.get(i).getEndTime()-nowTime;
            tem.allowed = allAllowedContest.contains(contests.get(i));
            contents.add(tem);
        }
        response.setContent(contents);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> getContestRank (int contestId) {

        Response response = new Response();
        response.setCode(0);
        if(contestMapper.getOneContest(contestId)==null){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        };
        int classId=contestMapper.getClassByContest(contestId);
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        if(responseEntity1 != null){
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
            ResponseEntity responseEntity3 = authService.checkPermission("0-"+classId);
            if (responseEntity2 !=null&&responseEntity3 !=null){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        List<UserResult>userResults=recordMapper.getContestResult(contestId);
        List<UserResult>nameScore=recordMapper.getNameScore(contestId);
        HashMap<String,Object>hashMap=new HashMap<>();
        List<Problem>problems=problemMapper.getContestProblem(contestId);
        List<String>shown=new LinkedList<>();
        for(Problem problem:problems){
            shown.add(problem.getShownId());
        }
        hashMap.put("problems",shown);
        if(userResults.size()==0){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        HashMap<String,Object []>fast=new HashMap<>();
        for (UserResult userResult:userResults) {
            fast.put(userResult.getUserId()+"|"+userResult.getShownId(),new Object[]{userResult.getScore(),userResult.getTotalScore(),userResult.getTime()});
        }
        List<HashMap<String,Object>>list=new LinkedList<>();
        for (UserResult name:nameScore) {
            String Totalcolor = "GREEN";
            long Totaltime = 0;
            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("userName",name.getUserName());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            for(Problem problem:problems) {
                HashMap<String, Object> hashMap2 = new HashMap<>();
                if (!fast.containsKey(name.getUserId() + "|" + problem.getShownId())) {
                    hashMap2.put("time", 0);
                    hashMap2.put("score", 0);
                    hashMap2.put("color", "RED");
                    hashMap1.put(problem.getShownId(), hashMap2);
                    continue;
                } else {
                    Object[] arr = fast.get(name.getUserId() + "|" + problem.getShownId());

                    int a = (Integer) arr[0];
                    int b = (Integer) arr[1];
                    String color;
                    if (a == 0) {
                        Totalcolor= "ORANGE";
                        color = "RED";
                    } else if (a == b) {
                        color = "GREEN";
                    } else {
                        Totalcolor= "ORANGE";
                        color = "ORANGE";
                    }
                    Totaltime+=(long)arr[2];
                    hashMap2.put("time", arr[2]);
                    hashMap2.put("score", arr[0]);
                    hashMap2.put("color", color);
                    hashMap1.put(problem.getShownId(), hashMap2);
                }
            }
            if(Totaltime==0){
                Totalcolor = "RED";
            }
            hashMap3.put("time", Totaltime);
            hashMap3.put("score", name.getScore());
            hashMap3.put("color", Totalcolor);
            hashMap1.put("totalScore",hashMap3);
            list.add(hashMap1);
        }
        hashMap.put("tableData",list);
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    public ResponseEntity<?> getAcceptedCode (int contestId) {
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        int classId=contestMapper.getClassByContest(contestId);
        ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
        if (responseEntity1!=null&&responseEntity2!=null){
            return responseEntity2;
        }
        List<UserResult>userResults=recordMapper.getLatestContestResult(contestId);
        File file1 = new File(StpUtil.getLoginId()+".zip");
        try {
        if(!file1.exists())
            file1.createNewFile();
        FileOutputStream fOutputStream = new FileOutputStream(file1);
        ZipOutputStream zoutput = new ZipOutputStream(fOutputStream);
        for (UserResult userResult:userResults){
                ZipEntry zEntry = new ZipEntry(userResult.getUserId() + "-" + userResult.getShownId()+".txt");
                zoutput.putNextEntry(zEntry);
                zoutput.write(recordMapper.getCode(contestId,userResult.getUserName(),userResult.getShownId(),userResult.getTime()).getBytes());
               // zoutput.close();
        }
            zoutput.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        String filePath = StpUtil.getLoginId()+".zip";
        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        try {
            return ResponseEntity.ok().headers(headers).contentLength(file.contentLength()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(file.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }return null;
    }

    public ResponseEntity<?> getStudentScore (int contestId) throws IOException {
        List<UserResult> userResults=recordMapper.getContestResult(contestId);
        HashMap<String, ArrayList<UserResult>> userScore = new HashMap<>();
        for(int i = 0;i<userResults.size();i++){
            if(!userScore.containsKey(userResults.get(i).getUserId())){
                userScore.put(userResults.get(i).getUserId(),new ArrayList<>());
            }
            userScore.get(userResults.get(i).getUserId()).add(userResults.get(i));
        }


        String filePath = StpUtil.getLoginId()+".csv";
        BufferedWriter writeText = new BufferedWriter(new FileWriter(filePath));
        String headRow ="id,";
        String[] names = userScore.keySet().toArray(new String[0]);

        for(int i =0;i<userScore.keySet().size();i++){
            writeText.newLine();
//            double totalScore =0;
            String row = names[i] +",";
            userScore.get(names[i]).sort(Comparator.comparing(UserResult::getShownId));
            if(i==0){
                for(int j =0 ; j< userScore.get(names[i]).size();j++){
                    headRow+= userScore.get(names[i]).get(j).getShownId() + ",";
                }
                headRow+= "totalScore\n";
                writeText.write(headRow);
            }
            int total=0;
            for(int j =0  ; j<userScore.get(names[i]).size();j++){
                row += userScore.get(names[i]).get(j).getScore();
                total+=userScore.get(names[i]).get(j).getScore();
            }
            row =row+ ","+total;
            writeText.write(row);
        }
        writeText.flush();
        writeText.close();

        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        try {
            return ResponseEntity.ok().headers(headers).contentLength(file.contentLength()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(file.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }return null;
    }

}
