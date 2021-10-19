package com.ooad.oj_backend.service;

import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.Paper;
import com.ooad.oj_backend.mybatis.entity.User;
import com.ooad.oj_backend.mybatis.entity.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthService authService;

    public ResponseEntity<?> addUser(String id,String name, String passWord, String mail) {
       ResponseEntity responseEntity=authService.checkPermission("1-0");
       if(responseEntity!=null)return responseEntity;
        Response response=new Response();
        User user=userMapper.getOne(id);
        if(user!=null){
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
    public ResponseEntity<?> getUsersInformation(int page,int itemsPerPage,String search) {
        List<UserView> users=userMapper.getAllByPage((page-1)*itemsPerPage,itemsPerPage);
        List<User>users1= userMapper.getAll();
        Paper<UserView> paper=new Paper<>();
        paper.setItemsPerPage(itemsPerPage);
        paper.setPage(page);
        paper.setTotalAmount(users1.size());
        paper.setTotalPage(users1.size()/itemsPerPage);
        paper.setList(users);
        Response response=new Response(0,"",paper);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> addBatchUser(MultipartFile multipartFile){
        ResponseEntity responseEntity=authService.checkPermission("1-0");
        if(responseEntity!=null)return responseEntity;
        File file = new File("./"+multipartFile.getOriginalFilename());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            multipartFile.transferTo(file);
        }catch (IOException e){
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Response response=new Response();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
