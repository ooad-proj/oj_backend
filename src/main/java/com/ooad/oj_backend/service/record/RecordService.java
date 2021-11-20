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
        if (!StpUtil.isLogin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Result> results = judgerService.getResultFromRedis(recordId);
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
        Response response = new Response();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("totalNum",results.size());
        hashMap.put("correctNum",(int) results.stream().filter(Result::isCorrect).count());
        hashMap.put("records",results);
        hashMap.put("finialResult",finialResult);

        response.setContent(hashMap);
        response.setCode(0);
       if(!judgerService.judgeRunning(recordId)){
           response.setCode(1);
       }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> getStandardTestRecord(String recordId){
        if (!StpUtil.isLogin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Result[] results = judgerService.getTestResult(recordId);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("standardResult",results[1]);
        hashMap.put("userResult",results[0]);
        Response response = new Response();
        response.setContent(hashMap);
        response.setCode(0);
        if(!judgerService.testTestCaseRunning(recordId)){
            response.setCode(1);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //TODO
    public void addResult(com.ooad.oj_backend.mybatis.entity.Result result, List<Result> checkPoints) {

    }
}

