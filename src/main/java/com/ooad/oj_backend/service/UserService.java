package com.ooad.oj_backend.service;

import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.AuthMapper;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthMapper authMapper;

    public ResponseEntity<?> addUser(String id,String name, String passWord, String mail) {
       ResponseEntity responseEntity=authService.checkPermission("1-0");
       if(responseEntity!=null)return responseEntity;
        Response response=new Response();
        User user=userMapper.getOne(id);
        if(id.equals("")||passWord.equals("")){
            response.setCode(-2);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        if(user!=null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        if(!checkPassWord(passWord)){
            response.setCode(-3);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        User newUser=new User();
        newUser.setId(id);
        newUser.setPassword(passWord);
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
        if(StpUtil.getLoginId().equals(id)){
            response.setCode(-100);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        User user=userMapper.getOne(id);
        if(user==null){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        authMapper.deletePeople(id);
        userMapper.delete(id);
        response.setCode(0);
        response.setMsg("delete success");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    public ResponseEntity<?> updateUser(String id,String name,String password,String mail) {
        ResponseEntity responseEntity=authService.checkPermission("1-0");
        if(responseEntity!=null)return responseEntity;
        Response response=new Response();
        User oldUser=userMapper.getOne(id);
        if(oldUser==null||!checkPassWord(password)){
            response.setCode(-1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        User user=new User();
        user.setId(id);
        user.setName(name);
        user.setPassword(password);
        user.setMail(mail);
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
        if(!StpUtil.isLogin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(search.equals("")) {
            List<UserView> users = userMapper.getAllByPage((page - 1) * itemsPerPage, itemsPerPage);
            int count=userMapper.getAll();
            if(StpUtil.getRoleList().get(0).equals("teacher")) {
                for (UserView userView : users) {
                    userView.setEditable(true);
                   Auth auth=authMapper.getTeacher(userView.getId());
                   if(auth==null){
                       userView.setDeletable(true);
                   }
                }
            }
            Paper<UserView> paper = new Paper<>();
            paper.setItemsPerPage(users.size());
            paper.setPage(page);
            paper.setTotalAmount(count);
            paper.setTotalPage((count/ itemsPerPage) + (((count % itemsPerPage) == 0) ? 0 : 1));
            paper.setList(users);
            Response response = new Response(0, "", paper);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }List<UserView> userList=userMapper.Search(search);
        if(StpUtil.getRoleList().get(0).equals("teacher")) {
            for (UserView userView : userList) {
                userView.setEditable(true);
                Auth auth=authMapper.getTeacher(userView.getId());
                if(auth==null){
                    userView.setDeletable(true);
                }
            }
        }
        Paper<UserView> paper = new Paper<>();
        if(userList!=null) {
            paper.setItemsPerPage(1);
            paper.setPage(1);
            paper.setTotalAmount(1);
            paper.setTotalPage(1);
            paper.setList(userList);
        }else {
            paper.setItemsPerPage(0);
            paper.setPage(1);
            paper.setTotalAmount(0);
            paper.setTotalPage(1);
            paper.setList(new LinkedList<>());
        }
            Response response = new Response(0, "", paper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public ResponseEntity<?> addBatchUser(MultipartFile multipartFile){
        ResponseEntity responseEntity=authService.checkPermission("1-0");
        if(responseEntity!=null)return responseEntity;
        try {
            Path p= Paths.get(Config.path);
            Files.createDirectories(p);
            Files.createDirectories(p.resolve((String) StpUtil.getLoginId()));
            File file = new File(Config.path+File.separator+(String) StpUtil.getLoginId()+File.separator+multipartFile.getOriginalFilename());
            if (!file.exists()) {
                file.createNewFile();
            }
            try {
                multipartFile.transferTo(file);//保存文件
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String line;
            int judge=0;
            BufferedReader reader=new BufferedReader(new FileReader(file));
            List<AddResult> addResults=new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                String[] content = line.split(",");
                AddResult addResult=new AddResult();
                User temp=userMapper.getOne(content[0]);
                addResult.setUserId(content[0]);
                if(temp!=null){
                    addResult.setStatus(-1);
                    addResults.add(addResult);
                    judge=-1;
                    continue;
                }
                if(content[0].equals("")||content[3].equals("")){
                    addResult.setStatus(-2);
                    addResults.add(addResult);
                    judge=-1;
                    continue;
                }
                if(!checkPassWord(content[3])){
                    addResult.setStatus(-3);
                    addResults.add(addResult);
                    judge=-1;
                    continue;
                }
                User user=new User();
                user.setId(content[0]);
                if(content[1].equals("")){
                    user.setName(content[0]);
                }
                else user.setName(content[1]);
                user.setMail(content[2]);
                user.setPassword(content[3]);
                userMapper.insert(user);
                addResult.setStatus(0);
                addResults.add(addResult);
            }
            Response response=new Response();
            response.setContent(addResults);
            response.setCode(judge);
            file.delete();
            File file1=new File(String.valueOf(p.resolve((String) StpUtil.getLoginId())));
            file1.delete();
            reader.close();
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (IOException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public static boolean checkPassWord(String passWord){
        int length=passWord.length();
        if(length<3||length>20){
            return false;
        }
        String regex = "^[0-9a-zA-Z]{1,}$";
        return passWord.matches(regex);
    }
}
