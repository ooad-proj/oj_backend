package com.ooad.oj_backend.service.record;

import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ContestMapper;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mapper.record.RecordMapper;
import com.ooad.oj_backend.mapper.user.UserMapper;
import com.ooad.oj_backend.mybatis.entity.*;
import com.ooad.oj_backend.rabbitmq.entity.Result;
import com.ooad.oj_backend.service.JudgerService;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecordService {


    @Autowired
    private AuthService authService;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private JudgerService judgerService;

    public ResponseEntity<?> getRecord(String recordId) {
        Response response = new Response();
        response.setCode(0);
        if(!judgerService.judgeRunning(recordId)){
            response.setCode(1);
        }
        if (!StpUtil.isLogin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }



        List<Result> results;
        if (judgerService.judgeRunning(recordId)) {
            results = judgerService.getResultFromRedis(recordId);
        } else {
            results = getResultFromSql(recordId);
            if (results == null || results.size() == 0) results = judgerService.getResultFromRedis(recordId);
        }
        com.ooad.oj_backend.mybatis.entity.Result result=recordMapper.getResultAndCode(recordId);
        if(result!=null&&result.getProblemId()!=0){
            int classId=problemMapper.getGroupId(result.getProblemId());
            ResponseEntity responseEntity = authService.checkPermission("1-0");
            ResponseEntity responseEntity2 = authService.checkPermission("1-" +classId);
            if(responseEntity!=null&&responseEntity2!=null){
                if(!StpUtil.getLoginId().equals(result.getUserId())){
                    return responseEntity;
                }
            }
        }

        int totalNum = results.size();
        int correctNum = (int) results.stream().filter(Result::isCorrect).count();
        com.ooad.oj_backend.mybatis.entity.Result results1=recordMapper.getResultAndCode(recordId);
        String finialResult="";
        boolean flag = true;
        if(correctNum != totalNum){
            for(int i =0 ;i<results.size();i++){
                if(!results.get(i).isCorrect()){
                    finialResult  = results.get(i).getCode();
                    flag = false;
                    break;
                }
            }
        }
        if(flag){
            finialResult = "AC";
        }

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("totalNum",results.size());
        if (results1 != null) hashMap.put("code",results1.getCode());
        hashMap.put("correctNum",(int) results.stream().filter(Result::isCorrect).count());
        hashMap.put("records",results);
        hashMap.put("finialResult",finialResult);

        response.setContent(hashMap);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> getStandardTestRecord(String recordId){
        if (!StpUtil.isLogin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response = new Response();
        response.setCode(0);
        HashMap<String,Object>hashMap=new HashMap<>();
        Result[] results = judgerService.getTestResult(recordId);
        if(results!=null) {
            hashMap.put("standardResult", results[1]);
            hashMap.put("userResult", results[0]);
            response.setCode(1);
        }
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> getResult(String userId,String problemId,String stateCode,String contestId,String groupId,int page,int itemsPerPage) {
        if (!StpUtil.isLogin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response = new Response();
        response.setCode(0);
        Paper<com.ooad.oj_backend.mybatis.entity.Result>paper=new Paper<>();
//        stateCode=stateCode.toLowerCase();
        List<com.ooad.oj_backend.mybatis.entity.Result>results=recordMapper.getResult(userId,problemId,stateCode,(page - 1) * itemsPerPage, itemsPerPage,contestId,groupId);
        /*int total=0;
        boolean judge=problemId.matches(regex);
        if(judge) {*/
           int total = recordMapper.getResultNum(userId,problemId,stateCode,contestId,groupId);
       // }
        paper.setItemsPerPage(results.size());
        paper.setPage(page);
        paper.setTotalAmount(total);
        paper.setTotalPage((total/ itemsPerPage) + (((total % itemsPerPage) == 0) ? 0 : 1));
        paper.setList(results);
        response.setContent(paper);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> getRank(int page,int itemsPerPage) {
        Response response = new Response();
        response.setCode(0);
        Paper<Rank>paper=new Paper<>();
        List<Rank>results=recordMapper.getRank((page - 1) * itemsPerPage, itemsPerPage);
        int total=recordMapper.getRankNum();
        paper.setItemsPerPage(results.size());
        paper.setPage(page);
        paper.setTotalAmount(total);
        paper.setTotalPage((total / itemsPerPage) + (((total % itemsPerPage) == 0) ? 0 : 1));
        paper.setList(results);
        response.setContent(paper);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    //TODO
    public void addResult(com.ooad.oj_backend.mybatis.entity.Result result, List<Result> checkPoints) {
        System.out.println(result.getResultId());
        try{
        recordMapper.addResult(result);
        for(Result result1:checkPoints){
            recordMapper.addCheckpoint(result1,result.getResultId());
        }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public List<Result> getResultFromSql(String submitId) {
        return recordMapper.getCheckpoint(submitId);
    }
}

