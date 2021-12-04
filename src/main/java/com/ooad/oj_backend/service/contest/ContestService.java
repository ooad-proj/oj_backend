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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Problem> problems = problemMapper.getContestProblem(contestId);
        //TODO: get myScore and score
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


    public ResponseEntity<?> addContest(int groupId , String title, String description, long startTime, long endTime) {
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

    public ResponseEntity<?> modifyContest(int contestId, String title, String description, long startTime, long endTime) {
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
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = (String) StpUtil.getLoginId();
        Response response = new Response();
        long nowTime = System.currentTimeMillis();
        List<Contest> contests = contestMapper.getCloseContest(nowTime);
        List<Contest> sortedContest = contests.stream().sorted(Comparator.comparing( Contest::getEndTime)).limit(amount).collect(Collectors.toList());
        List<Content> contents = new ArrayList<>();
        List<Contest> allAllowedContest = contestMapper.getAllowedContest(userId);
        for(int i =0;i<sortedContest.size();i++){
            Content tem = new Content();
            tem.contestId = contests.get(i).getId();
            tem.title = contests.get(i).getTitle();
            tem.timeLeft =nowTime- contests.get(i).getEndTime();
            tem.allowed = allAllowedContest.contains(contests.get(i));
            contents.add(tem);
        }
        response.setContent(contents);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> getContestRank (int contestId) {
<<<<<<< Updated upstream
//        recordMapper.getContestResult();
        return null;
=======
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
            if (responseEntity2 !=null){
                return responseEntity2;
            }
        }
        List<UserResult>userResults=recordMapper.getContestResult(contestId);
        List<UserResult>nameScore=recordMapper.getNameScore(contestId);
        HashMap<String,Object>hashMap=new HashMap<>();
        List<Problem>problems=problemMapper.getContestProblem(contestId);

        for (Problem problem:problems){
            hashMap.put("problems",problem);
        }
        if(userResults.size()==0){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        HashMap<String,Object []>fast=new HashMap<>();
        for (UserResult userResult:userResults) {
            fast.put(userResult.getUserName()+"|"+userResult.getShownId(),new Object[]{userResult.getScore(),userResult.getTotalScore(),userResult.getTime()});
        }
        List<HashMap<String,Object>>list=new LinkedList<>();
        for (UserResult name:nameScore) {
            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("userName",name.getUserName());
            hashMap1.put("totalScore",name.getScore());
            for(Problem problem:problems){
                Object []arr=fast.get(name.getUserName()+"|"+problem.getShownId());
                HashMap<String, Object> hashMap2 = new HashMap<>();
                int a=(Integer) arr[0];
                int  b=(Integer) arr[1];
                String color;
                if(a==0) {
                    color = "RED";
                }else if(a==b){
                    color = "GREEN";
                }else{
                    color = "YELLOW";
                }
                hashMap2.put("time",arr[2]);
                hashMap2.put("score",arr[0]);
                hashMap2.put("color",color);
                hashMap1.put(problem.getShownId(),hashMap2);
            }
            list.add(hashMap1);
        }
        hashMap.put("tableData",list);
        return new ResponseEntity<>(response, HttpStatus.OK);
>>>>>>> Stashed changes
    }
    public ResponseEntity<?> getAcceptedCode (int contestId) {
        return null;
    }
    public ResponseEntity<?> getStudentScore () {
        return null;
    }
}
