package com.ooad.oj_backend.service.submit;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mapper.record.RecordMapper;
import com.ooad.oj_backend.mapper.submit.SubmitMapper;
import com.ooad.oj_backend.mapper.user.AuthMapper;
import com.ooad.oj_backend.mapper.user.UserMapper;
import com.ooad.oj_backend.mybatis.entity.*;
import com.ooad.oj_backend.rabbitmq.MqUtil;
import com.ooad.oj_backend.rabbitmq.entity.JudgeDetail;
import com.ooad.oj_backend.rabbitmq.entity.Template;
import com.ooad.oj_backend.service.JudgerService;
import com.ooad.oj_backend.service.user.AuthService;
import com.ooad.oj_backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class SubmitService {

    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private ProblemMapper problemMapper;
    @Autowired
    private JudgerService judgerService;

    public ResponseEntity<?> submitCode(int problemId,String language,String code) {
        int groupId=problemMapper.getGroupId(problemId);
        Response response=new Response();
        response.setCode(0);
        if(groupId!=0) {
            ResponseEntity responseEntity = authService.checkPermission("0-" + groupId);
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
            if (responseEntity2 != null && responseEntity != null) {
                ResponseEntity responseEntity1 = authService.checkPermission("1-0");
                if (responseEntity1 != null) {
                    response.setCode(-2);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        }
        if(problemMapper.searchProblem(problemId)==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Problem problem=problemMapper.getDetailedProblem(problemId);
        List<Template>submitTemplates=problemMapper.getTemplate(problemId);
        JudgeDetail detail=new JudgeDetail(language,code,(int)problem.getTimeLimit(),(int)problem.getSpaceLimit(),submitTemplates);
        String UUID=judgerService.judge(problem.getTestCase(),detail,problemId);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("submitId",UUID);
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> standardAnsTest(int problemId,String language,String code,String testcase){
        int groupId=problemMapper.getGroupId(problemId);
        Response response=new Response();
        response.setCode(0);
        if(groupId!=0) {
        ResponseEntity responseEntity = authService.checkPermission("0-"+groupId);
        ResponseEntity responseEntity2 = authService.checkPermission("1-"+groupId);
        if (responseEntity2 != null && responseEntity!=null){
            ResponseEntity responseEntity1 = authService.checkPermission("1-0");
            if (responseEntity1 != null ){
                response.setCode(-2);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        }
        if(problemMapper.searchProblem(problemId)==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if(problemMapper.searchStandardAnswerByProblem(problemId)==0){
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        List<Answer> answers=problemMapper.getStandardAnswerByProblem(problemId);
        Problem problem=problemMapper.getDetailedProblem(problemId);
        List<Template>submitTemplates=problemMapper.getTemplate(problemId);
        JudgeDetail userJudgeDetail=new JudgeDetail(language,code,(int)problem.getTimeLimit(),(int)problem.getSpaceLimit(),submitTemplates);
        JudgeDetail answerJudgeDetail=new JudgeDetail(answers.get(0).getLanguage(),answers.get(0).getCode(),(int)problem.getTimeLimit(),(int)problem.getSpaceLimit(),submitTemplates);
        String UUID=judgerService.testTestCase(testcase,userJudgeDetail,answerJudgeDetail);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("submitId",UUID);
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> AskIfhaveAnswer(int problemId) {
        int groupId=problemMapper.getGroupId(problemId);
        Response response=new Response();
        response.setCode(0);
        if(groupId!=0) {
            ResponseEntity responseEntity = authService.checkPermission("0-" + groupId);
            ResponseEntity responseEntity2 = authService.checkPermission("1-" + groupId);
            if (responseEntity2 != null && responseEntity != null) {
                ResponseEntity responseEntity1 = authService.checkPermission("1-0");
                if (responseEntity1 != null) {
                    response.setCode(-2);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        }
        if(problemMapper.searchProblem(problemId)==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        HashMap<String,Object>hash=new HashMap<>();
        int haveAnswer=problemMapper.searchStandardAnswerByProblem(problemId);
        hash.put("haveAnswer",haveAnswer!=0);
        response.setContent(hash);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getSubmitNum(String userId) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateformat.format(System.currentTimeMillis());
        LocalDate localDate = LocalDate.parse(dateStr);
        LocalDate localDate1 =localDate.minusDays(30);
        Long milliSecond = localDate1.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();

        String[] label = new String[30];
        for(int i=0;i<30;i++){
            label[0]= String.valueOf(localDate1);
            localDate1 =localDate1.plusDays(1);
        }
        int[] data = new  int[30];

        if(userId.isEmpty()){
            List<Long> submitTimes = recordMapper.getAllSubmitNum(userId,milliSecond);
            for(int i =0;i<submitTimes.size();i++){
                int index = (int) ( (submitTimes.get(i)-milliSecond)/(86400000) );
                data[index]++;
            }
        }else {
            List<Long> submitTimes = recordMapper.getSubmitNum(userId,milliSecond);
            for(int i =0;i<submitTimes.size();i++){
                int index = (int) ( (submitTimes.get(i)-milliSecond)/(86400000) );
                data[index]++;
            }
        }
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("label",label);
        hashMap.put("data",data);
        Response response=new Response();
        response.setCode(0);
        response.setContent(hashMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
