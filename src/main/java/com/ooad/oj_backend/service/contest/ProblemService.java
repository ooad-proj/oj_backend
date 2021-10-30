package com.ooad.oj_backend.service.contest;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mybatis.entity.*;
import com.ooad.oj_backend.service.user.AuthService;
import org.apache.ibatis.javassist.tools.rmi.Sample;
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
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private AuthService authService;
    public ResponseEntity<?> getProblem(String search,int page,int itemsPerPage) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        String userId="";
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            userId="and (userId='"+ StpUtil.getLoginId()+"' or contest.id=0) ";
        }
        List<ProblemView>problemViews=problemMapper.getProblem(search,userId,(page - 1) * itemsPerPage,itemsPerPage);
        int count=problemMapper.getProblemNumber(search,userId);
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
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            userId="and (userId='"+ StpUtil.getLoginId()+"' and privilege=1 or contest.id=0)";
        }
        List<ProblemView>problemViews=problemMapper.getProblem(search,userId,(page - 1) * itemsPerPage,itemsPerPage);
        int count=problemMapper.getProblemNumber(search,userId);
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
        HashMap<String,Object>hashMap=new HashMap<>();
        Problem problem=problemMapper.getDetailedProblem(problemId);
        CreatorAndGroup creatorAndGroup=problemMapper.getCreatorAndGroup(problemId);
        Samples samples=new Samples();
        ScoreRule scoreRule=new ScoreRule();
        scoreRule.setPunishRule(problem.getPunishRule());
        scoreRule.setAllowPartial(problem.isAllowPartial());
        scoreRule.setTotalScore(problem.getTotalScore());
        samples.setInput(problem.getInput());
        samples.setOutput(problem.getOutput());
        JSONObject jsonObject = JSONUtil.parseObj(problem.getAllowedLanguage());
        String[] language= (String[]) jsonObject.get("allowedLanguage");
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
        hashMap.put("testCaseId",problem.getContestId());
        hashMap.put("ScoreRule",scoreRule);
        hashMap.put("creatorId",creatorAndGroup.getClassId());
        hashMap.put("creatorName",creatorAndGroup.getClassName());
        hashMap.put("groupId",creatorAndGroup.getGroupId());
        hashMap.put("groupName",creatorAndGroup.getGroupName());
        response.setCode(0);
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> addProblem(int contestId, int shownId, String title, ScoreRule scoreRule,
                                        Samples[] samples, String description, String inputFormat, String outputFormat,
                                        SubmitTemplate[] submitTemplate,String tips,String timeLimit,String spaceLimit,
                                        String allowedLanguage,String testCaseId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    }
    public ResponseEntity<?> deleteProblem(int problemId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        int number=problemMapper.searchProblem(problemId);
        if(number==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        problemMapper.deleteProblem(problemId);
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> updateProblem(int problemId, int shownId, String title, ScoreRule scoreRule,
                                           Samples[] samples, String description, String inputFormat, String outputFormat,
                                           SubmitTemplate[] submitTemplate,String tips,String timeLimit,String spaceLimit,
                                           String allowedLanguage,String testCaseId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        int contestId=problemMapper.getContestId(problemId);
        problemMapper.updateProblem(contestId,shownId,title,scoreRule.getTotalScore(),scoreRule.isAllowPartial(),scoreRule.getPunishRule()
                ,description,inputFormat,
                outputFormat,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId);
        problemMapper.deleteSamples(problemId);
        for(Samples sample:samples){
            problemMapper.addSample(problemId,sample.getInput(),sample.getOutput());
        }
        problemMapper.deleteSubmitTemplates(problemId);
        for(SubmitTemplate submitTemplate1:submitTemplate){
            problemMapper.addSubmitTemplate(problemId,submitTemplate1.getLanguage(),submitTemplate1.getCode());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> addTestCase(int groupId , MultipartFile multipartFile) {
        //TODO testCase haven't be tested.

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

        String name = multipartFile.getOriginalFilename();
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
            if(nameList.size() %2==0){
                flag = false;
            }
            int testCaseAmount = (nameList.size()-1)/2;
            for(int i =1;i<nameList.size();i++){
                String[] tem1 = nameList.get(i).split("\\.");
                if(!(tem1[0].equals(name) && tem1[1].equals("in"))){
                    flag = false;
                    break;
                }
                String[] tem2 = nameList.get(i+1).split("\\.");
                if(!(tem2[0].equals(name) && tem2[1].equals("out"))){
                    flag = false;
                    break;
                }
                i++;
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
            response.setContent(universallyUniqueID);
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
        problemMapper.deleteAnswer(answerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
