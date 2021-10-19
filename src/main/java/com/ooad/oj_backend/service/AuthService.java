package com.ooad.oj_backend.service;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import com.ooad.oj_backend.mybatis.entity.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
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
    public Response Logout() {
        Response response=new Response();
        if(!StpUtil.isLogin()){
            response.setCode(-1);
            return response;
        }
        StpUtil.logout();
        response.setCode(0);
        return response;
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
    public ResponseEntity<?> changeSelfInformation(String name,String mail) {
        Response response=new Response();
        response.setCode(0);
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String id=(String)StpUtil.getLoginId();
        User user=new User();
        user.setId(id);
        user.setName(name);
        user.setMail(mail);
        userMapper.update(user);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> checkRole(){
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
            String role=StpUtil.getRoleList().get(0);
            Response response=new Response();
            response.setCode(0);
            response.setContent(role);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> getSelfInformation() {
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String id=(String)StpUtil.getLoginId();
        User user=userMapper.getOne(id);
        UserView userView=new UserView();
        userView.setId(id);
        userView.setName(user.getName());
        userView.setMail(user.getMail());
        Response response=new Response();
        response.setCode(0);
        response.setContent(userView);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    /*
    not finished
    * */
    public ResponseEntity<?> getGroupInformation() {
        Response response=new Response();
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> changePassWord(String oldPassWord,String newPassWord) {
        Response response=new Response();
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String id=(String)StpUtil.getLoginId();
        User user=userMapper.getOne(id);
        if(!user.getPassWord().equals(oldPassWord)){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        user.setPassWord(newPassWord);
        userMapper.update(user);
        response.setCode(0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public Response isLogin() {
        Response response=new Response();
        response.setCode(0);
        response.setContent(false);
        if(StpUtil.isLogin())
        response.setContent(true);
        return response;
    }
   /*public boolean isLogin() {
      return StpUtil.isLogin();
   }*/
}