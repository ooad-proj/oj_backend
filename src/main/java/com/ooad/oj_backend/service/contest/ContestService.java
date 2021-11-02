package com.ooad.oj_backend.service.contest;

import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.contest.ContestMapper;
import com.ooad.oj_backend.mapper.contest.ProblemMapper;
import com.ooad.oj_backend.mapper.user.AuthMapper;
import com.ooad.oj_backend.mapper.user.UserMapper;
import com.ooad.oj_backend.mybatis.entity.Contest;
import com.ooad.oj_backend.mybatis.entity.Paper;
import com.ooad.oj_backend.mybatis.entity.Problem;
import com.ooad.oj_backend.mybatis.entity.UserView;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public ResponseEntity<?> getContestInformation(int contestId) {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Response response=new Response();
        if(StpUtil.isLogin()){
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
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

        total = contestMapper.getTotalNum(search);

        if(responseEntity1==null){ //get for teacher
            contests = contestMapper.getAllContest((page - 1) * itemsPerPage,itemsPerPage,search);
        }
        else {
            //get for assistant
            String userId = (String) StpUtil.getLoginId();
            contests = contestMapper.getManagementContest(userId,search);
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
}
