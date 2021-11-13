package com.ooad.oj_backend.service.submit;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.user.AuthMapper;
import com.ooad.oj_backend.mapper.user.UserMapper;
import com.ooad.oj_backend.mybatis.entity.Paper;
import com.ooad.oj_backend.mybatis.entity.RoleView;
import com.ooad.oj_backend.mybatis.entity.User;
import com.ooad.oj_backend.mybatis.entity.UserView;
import com.ooad.oj_backend.rabbitmq.MqUtil;
import com.ooad.oj_backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class SubmitService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthMapper authMapper;

    public ResponseEntity<?> submitCode(int problemId) {
        return null;
    }

    public ResponseEntity<?> standardAnsTest(int problemId, String testcase){
        return null;
    }

    public ResponseEntity<?> AskIfhaveAnswer(int problemId) {
        return null;
    }
}
