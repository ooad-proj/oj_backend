package com.ooad.oj_backend.service.contest;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ContestMapper;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mybatis.entity.*;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private ContestMapper contestMapper;
    public ResponseEntity<?> getProblem(String search,int page,int itemsPerPage) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        List<ProblemView>problemViews;
        String userId="";
        int count;
        long time=System.currentTimeMillis();
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            userId="and ((userId='"+ StpUtil.getLoginId()+"' and privilege==1 or p.contestId=0) or (userId='"+ StpUtil.getLoginId()+"' and privilege==0 and contest.startTime<"+time+" or p.contestId=0))";
            count=problemMapper.getProblemNumber(search,userId);
            problemViews=problemMapper.getProblem(search,userId,(page - 1) * itemsPerPage,itemsPerPage);
        }else {
            problemViews = problemMapper.getProblem1(search, (page - 1) * itemsPerPage, itemsPerPage);
            count=problemMapper.getProblem1Number(search);
        }

        Paper paper=new Paper<ProblemView>(page,problemViews.size(),count,(count / itemsPerPage) + (((count % itemsPerPage) == 0) ? 0 : 1));
        paper.setList(problemViews);
        response.setCode(0);
        response.setContent(paper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> getProblemByPrivilege(String search,int page,int itemsPerPage) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Response response=new Response();
        String userId="";
        List<ProblemView>problemViews;
        int count;
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            userId="and (userId='"+ StpUtil.getLoginId()+"' and privilege=1)";
            count=problemMapper.getProblemNumber(search,userId);
            problemViews=problemMapper.getProblem(search,userId,(page - 1) * itemsPerPage,itemsPerPage);
        }else {
            problemViews = problemMapper.getProblem1(search, (page - 1) * itemsPerPage, itemsPerPage);
            count=problemMapper.getProblem1Number(search);
        }
        Paper paper=new Paper<ProblemView>(page,problemViews.size(),count,(count / itemsPerPage) + (((count % itemsPerPage) == 0) ? 0 : 1));
        paper.setList(problemViews);
        response.setCode(0);
        response.setContent(paper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> getDetailedProblem(int problemId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        response.setCode(0);
        int checkProblem=problemMapper.searchProblem(problemId);
        if(checkProblem==0){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        String userId="";
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            int classId=problemMapper.getGroupId(problemId);
            if(classId!=0) {
                ResponseEntity responseEntity1 = authService.checkPermission("0-" + classId);
                ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
                if (responseEntity2 != null && responseEntity1 != null) {
                    return responseEntity2;
                }
                if (responseEntity1 == null) {
                    int s = state(problemId);
                    if (s == -1) {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
        }
        HashMap<String,Object>hashMap=new HashMap<>();
        Problem problem=problemMapper.getDetailedProblem(problemId);
        CreatorAndGroup creatorAndGroup=problemMapper.getCreatorAndGroup(problemId);
        SubmitTemplate[] submitTemplates=problemMapper.getSubmitTemplate(problemId);
        Samples[] samples=problemMapper.getSamples(problemId);
        ScoreRule scoreRule=new ScoreRule();
        scoreRule.setPunishRule(problem.getPunishRule());
        scoreRule.setAllowPartial(problem.isAllowPartial());
        scoreRule.setTotalScore(problem.getTotalScore());
        JSONArray jsonObject = JSONUtil.parseArray(problem.getAllowedLanguage1());
        String[] language=new String[jsonObject.size()];
        for(int i=0;i<jsonObject.size();++i){
            language[i]=String.valueOf(jsonObject.get(i));
        }
        hashMap.put("shownId",problem.getShownId());
        hashMap.put("title",problem.getTitle());
        hashMap.put("description",problem.getDescription());
        hashMap.put("inputFormat",problem.getInputFormat());
        hashMap.put("outputFormat",problem.getOutputFormat());
        hashMap.put("samples",samples);
        hashMap.put("tips",problem.getTips());
        hashMap.put("timeLimit",problem.getTimeLimit());
        hashMap.put("spaceLimit",problem.getSpaceLimit());
        hashMap.put("allowedLanguage",language);
        hashMap.put("testCaseId",problem.getTestCaseId());
        hashMap.put("scoreRule",scoreRule);
        hashMap.put("creatorId",creatorAndGroup.getCreatorId());
        hashMap.put("creatorName",creatorAndGroup.getCreatorName());
        hashMap.put("contestId",creatorAndGroup.getContestId());
        hashMap.put("contestTitle",creatorAndGroup.getContestTitle());
        hashMap.put("groupId",creatorAndGroup.getGroupId());
        hashMap.put("groupName",creatorAndGroup.getGroupName());
        hashMap.put("submitTemplate",submitTemplates);
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /*public ResponseEntity<?> addProblem(int contestId, int shownId, String title, ScoreRule scoreRule,
                                        Samples[] samples, String description, String inputFormat, String outputFormat,
                                        SubmitTemplate[] submitTemplate,String tips,String timeLimit,String spaceLimit,
                                        String allowedLanguage,String testCaseId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        int check=problemMapper.checkProblemPrivilege("p.contestId="+contestId,(String) StpUtil.getLoginId());
        if(check==0){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Response response=new Response();
        int problemId=problemMapper.addProblem(contestId,shownId,title
                ,description,inputFormat,
                outputFormat,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId);
        problemMapper.addScoreRule(problemId,scoreRule.getTotalScore(),scoreRule.isAllowPartial(),scoreRule.getPunishRule());
        for(Samples sample:samples){
            problemMapper.addSample(problemId,sample.getInput(),sample.getOutput());
        }
        for(SubmitTemplate submitTemplate1:submitTemplate){
            problemMapper.addSubmitTemplate(problemId,submitTemplate1.getLanguage(),submitTemplate1.getCode());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/
    public ResponseEntity<?> addProblem(int contestId, Problem problem){
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        /*int check=problemMapper.checkProblemPrivilege("p.contestId="+contestId,(String) StpUtil.getLoginId());
        if(check==0){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }*/
        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String userId="";
        Response response=new Response();
        Contest contest=contestMapper.getOneContest(contestId);
        if(contest==null){
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            int classId=contestMapper.getClassByContest(contestId);
                ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
                if (responseEntity2 != null) {
                    return responseEntity2;
                }
        }
        if(check(problem)){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        String[]language=problem.getAllowedLanguage();
        String allowed=Convert.toStr(language);
        problemMapper.addProblem(contestId,problem,(String) StpUtil.getLoginId(),allowed);
        int problemId=problem.getProblemId();

        Samples[] samples=problem.getSamples();
        if(samples!=null){
            for(Samples sample:samples){
                problemMapper.addSample(problemId,sample.getInput(),sample.getOutput());
            }
        }
        SubmitTemplate[]submitTemplate=problem.getSubmitTemplate();
        if(submitTemplate!=null) {
            for (SubmitTemplate submitTemplate1 : submitTemplate) {
                problemMapper.addSubmitTemplate(problemId, submitTemplate1.getLanguage(), submitTemplate1.getCode());
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> deleteProblem(int problemId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
       /* int check=problemMapper.checkProblemPrivilege("p.problemId="+problemId,(String) StpUtil.getLoginId());
        if(check==0){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }*/
        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String userId="";
        Response response=new Response();
        int number=problemMapper.searchProblem(problemId);
        if(number==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            int classId=problemMapper.getGroupId(problemId);
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
            if (responseEntity2 != null) {
                return responseEntity2;
            }
        }
        problemMapper.deleteSample(problemId);
        problemMapper.deleteSubmitTemplates(problemId);
        problemMapper.deleteProblem(problemId);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> updateProblem(int problemId, Problem problem) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
      /* int check=problemMapper.checkProblemPrivilege("p.problemId="+problemId,(String) StpUtil.getLoginId());
        if(check==0){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }*/
        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Response response=new Response();
        if(problemMapper.searchProblem(problemId)==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        String userId="";
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            int classId=problemMapper.getGroupId(problemId);
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
            if (responseEntity2 != null) {
                return responseEntity2;
            }
        }
        if(check(problem)){
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        String[]language=problem.getAllowedLanguage();
        String allowed=Convert.toStr(language);
        problemMapper.updateProblem(problemId,problem,allowed);
        Samples[] samples=problem.getSamples();
        SubmitTemplate[]submitTemplate=problem.getSubmitTemplate();
        if(samples!=null) {
            problemMapper.deleteSample(problemId);
            for (Samples sample : samples) {
                problemMapper.addSample(problemId, sample.getInput(), sample.getOutput());
            }
        }
        if(submitTemplate!=null) {
            problemMapper.deleteSubmitTemplates(problemId);
            for (SubmitTemplate submitTemplate1 : submitTemplate) {
                problemMapper.addSubmitTemplate(problemId, submitTemplate1.getLanguage(), submitTemplate1.getCode());
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> addTestCase(int contestId , MultipartFile multipartFile) {

        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        if(responseEntity1 != null){
            int classId=contestMapper.getClassByContest(contestId);
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + classId);
            if (responseEntity2 !=null){
                return responseEntity2;
            }
        }

//        String name = multipartFile.getOriginalFilename();
        Response response=new Response();
        try {
            Path p= Paths.get(Config.testCaseStore);
            Files.createDirectories(p);
            String universallyUniqueID= UUID.randomUUID().toString().replace("-", "").toLowerCase();
            Files.createDirectories(p.resolve(universallyUniqueID));
            File file = new File(Config.testCaseStore+File.separator+File.separator+universallyUniqueID);
            if (!file.exists()) {
                file.createNewFile();
            }
            multipartFile.transferTo(file);
            ZipFile zipFile = new ZipFile(file);
            List<String> nameList = new ArrayList<>();
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();
                nameList.add(entry.getName());
            }
            Boolean flag =true;
            if(nameList.size() %2==1){
                flag = false;
            }
            ArrayList<String> inName = new ArrayList<>();
            ArrayList<String> outName = new ArrayList<>();
            int testCaseAmount = (nameList.size())/2;
            for(int i =0 ; i<nameList.size();i++){
                if( !( nameList.get(i).endsWith(".in") ||  nameList.get(i).endsWith(".out")) ){
                    flag = false;
                    response.setCode(-1);
                    file.delete();
                    return new ResponseEntity<>(response,HttpStatus.OK);
                }
            }
            for(int i =0;i<nameList.size();i++){
                String[] tem = nameList.get(i).split("\\.");
                if( tem[1].equals("in")){
                    inName.add(tem[0]);
                }
                if( tem[1].equals("out")){
                    outName.add(tem[0]);
                }
            }

            if(inName.size()!=outName.size()){
                flag=false;
            }else {
                loop:for(int i =0;i<inName.size();i++){
                    for(int j =0 ;j<inName.get(i).length();j++){
                        if( !(inName.get(i).charAt(j)>='1' || inName.get(i).charAt(j)<='9')){
                            flag=false;
                            break loop;
                        }
                    }
                    if(!outName.contains(inName.get(i))){
                        flag=false;
                        break;
                    }
                }
            }

            for(int i =0 ;i<inName.size();i++){
                if(!inName.contains(String.valueOf(i+1))){
                    flag = false;
                    break;
                }
                if(!outName.contains(String.valueOf(i+1))){
                    flag = false;
                    break;
                }
            }

            if (!flag){
                response.setCode(-1);
                file.delete();
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
            response.setCode(0);
            Map<String,Object> map = new HashMap<>();
            map.put("testCaseId",universallyUniqueID);
            map.put("testCaseAmount",testCaseAmount);
            response.setContent(map);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (IOException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> addAnswer(int problemId,String language,String code) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Response response=new Response();
        response.setCode(0);
        int problem=problemMapper.searchProblem(problemId);
        if(problem==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        int check=problemMapper.checkProblemPrivilege("p.problemId="+problemId,(String) StpUtil.getLoginId());
        if(check==0){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        problemMapper.addAnswer(problemId,language,code);
        response.setMsg("add success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> updateAnswer(int answerId,String language,String code) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Response response=new Response();
        response.setCode(0);
        Answer answer=problemMapper.getAnswerById(answerId);
        if(answer==null){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(role.equals("assistant")) {
            int check = problemMapper.checkAnswerPrivilege("answerId=" + answerId, (String) StpUtil.getLoginId());
            if (check == 0) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        problemMapper.updateAnswer(answerId,language,code);
        response.setMsg("update success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> getAnswer(int problemId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if(role.equals("assistant")) {
            int check = problemMapper.checkProblemPrivilege("p.problemId=" + problemId, (String) StpUtil.getLoginId());
            if (check == 0) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        Response response=new Response();
        response.setCode(0);
        int problem=problemMapper.searchProblem(problemId);
        if(problem==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        List<Answer> answer=problemMapper.getAnswerByProblem(problemId);
        response.setContent(answer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> deleteAnswer(int answerId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String role=StpUtil.getRoleList().get(0);
        if(!role.equals("teacher")&&!role.equals("assistant")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Response response=new Response();
        response.setCode(0);
        Answer answer=problemMapper.getAnswerById(answerId);
        if(answer==null){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(role.equals("assistant")) {
        int check=problemMapper.checkAnswerPrivilege("answerId="+answerId,(String) StpUtil.getLoginId());
        if(check==0){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        }
        problemMapper.deleteAnswer(answerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    static boolean check(Problem problem){
        return problem.getShownId()==null||problem.getTitle()==null||problem.getDescription()==null||problem.getTotalScore()<0
                ||problem.getInputFormat()==null||problem.getOutputFormat()==null||problem.getTimeLimit()<=0||problem.getSpaceLimit()<=0
                ||problem.getAllowedLanguage()==null||problem.getTestCaseId()==null;
    }
    int state(int problemId){
        Contest contest=problemMapper.getContestNumber(problemId);
        long start=contest.getStartTime();
        long end=contest.getEndTime();
        long time=System.currentTimeMillis();
        if(time<=start) {
            return -1;
        }if(time<end)
            return 0;
        return 1;
    }
}
