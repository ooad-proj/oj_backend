package com.ooad.oj_backend.service;

import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class UserService {
    private UserMapper userMapper;
    private AuthService authService;
    public ResponseEntity<?> addUser(String id,String name, String passWord, String mail) {
       ResponseEntity responseEntity=authService.checkPermission("1-0");
       if(responseEntity!=null)return responseEntity;
        Response response=new Response();
        User user=userMapper.getOne(id);
        if(user==null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        if(id==null||passWord==null){
            response.setCode(-2);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        User newUser=new User();
        newUser.setId(id);
        newUser.setPassWord(passWord);
        if(name==null){
            newUser.setName(id);
        }else newUser.setName(name);
        if(mail!=null){
            newUser.setMail(mail);
        }
        userMapper.insert(newUser);
        response.setCode(0);
        response.setMsg("add user success");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> deleteUser(String id) {
        ResponseEntity responseEntity=authService.checkPermission("1-0");
        if(responseEntity!=null)return responseEntity;
        Response response=new Response();
        User user=userMapper.getOne(id);
        if(user==null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        userMapper.delete(id);
        response.setCode(0);
        response.setMsg("delete success");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> updateUser(String id,User user) {
        ResponseEntity responseEntity=authService.checkPermission("1-0");
        if(responseEntity!=null)return responseEntity;
        Response response=new Response();
        User oldUser=userMapper.getOne(id);
        if(oldUser==null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        userMapper.update(user);
        response.setCode(0);
        response.setMsg("modify success");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> getUserInformation(String id) {
        ResponseEntity responseEntity=authService.checkPermission("1-0");
        if(responseEntity!=null)return responseEntity;
        User user=userMapper.getOne(id);
        Response response=new Response();
        if(user==null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setCode(0);
        response.setContent(user);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> getUsersInformation() {
        List<User> users=userMapper.getAll();
        Response response=new Response(0,"",users);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
