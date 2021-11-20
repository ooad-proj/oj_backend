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
        }

        int totalNum = results.size();
        int correctNum = (int) results.stream().filter(Result::isCorrect).count();
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
        if(!judgerService.testTestCaseRunning(recordId)){
            response.setCode(1);
        }
        HashMap<String,Object>hashMap=new HashMap<>();
        if (judgerService.judgeRunning(recordId)) {
            Result[]results = judgerService.getTestResult(recordId);
            hashMap.put("standardResult",results[1]);
            hashMap.put("userResult",results[0]);
        }else {
            List<Result>results = getResultFromSql(recordId);
            hashMap.put("standardResult",results.get(0));
            hashMap.put("userResult",results.get(1));
        }

        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //TODO
    public void addResult(com.ooad.oj_backend.mybatis.entity.Result result, List<Result> checkPoints) {
        recordMapper.addResult(result);
        for(Result result1:checkPoints){
            recordMapper.addCheckpoint(result1,result.getResultId());
        }
    }

    public List<Result> getResultFromSql(String submitId) {
        return recordMapper.getCheckpoint(submitId);
    }
}

