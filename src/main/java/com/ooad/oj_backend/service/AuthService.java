package com.ooad.oj_backend.service;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthService {
    @Autowired
    private UserMapper userMapper;
    public ResponseEntity<?> Login(String id, String passWord) {
        Response response=new Response();
        User user=userMapper.getOne(id);
        if(user==null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        if(user.getId().equals(id) && user.getPassWord().equals(passWord)) {
            StpUtil.login(id);
            response.setCode(0);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }else {
            response.setCode(-2);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
    public ResponseEntity<?> checkPermission(String permission){
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            StpUtil.checkPermission(permission);
        }catch (NotPermissionException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return null;
    }
}
