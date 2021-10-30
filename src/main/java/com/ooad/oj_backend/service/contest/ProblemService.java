package com.ooad.oj_backend.service.contest;

import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mybatis.entity.Paper;
import com.ooad.oj_backend.mybatis.entity.Problem;
import com.ooad.oj_backend.mybatis.entity.ProblemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;
    public ResponseEntity<?> getProblem(String search,int page,int itemsPerPage) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        String userId="";
        if(!StpUtil.getRoleList().get(0).equals("teacher")){
            userId="and userId="+ StpUtil.getLoginId();
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
            userId="and userId="+ StpUtil.getLoginId()+" and privilege=1";
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
        return null;
    }
    public ResponseEntity<?> addProblem(int contestId) {
        return null;
    }
    public ResponseEntity<?> deleteProblem(int problemId) {
        return null;
    }
    public ResponseEntity<?> updateProblem(int problemId) {
        return null;
    }
    public ResponseEntity<?> addTestCase() {
        return null;
    }
    public ResponseEntity<?> addAnswer(int problemId,String language,String code) {
        return null;
    }
    public ResponseEntity<?> updateAnswer(int problemId) {
        return null;
    }
    public ResponseEntity<?> getAnswer(int problemId) {
        return null;
    }
    public ResponseEntity<?> deleteAnswer(int problemId) {
        return null;
    }
}
