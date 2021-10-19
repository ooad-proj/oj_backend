package com.ooad.oj_backend.service;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.AuthMapper;
import com.ooad.oj_backend.mapper.GroupMapper;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.AddResult;
import com.ooad.oj_backend.mybatis.entity.Auth;
import com.ooad.oj_backend.mybatis.entity.Group;
import com.ooad.oj_backend.mybatis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private AuthService authService;

    public ResponseEntity<?> addGroup(int id, String name) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group group = groupMapper.getOne(id);
        if (group != null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Group newGroup = new Group();
        newGroup.setId(id);
        if (name == null) {
            newGroup.setName(String.valueOf(id));
        } else newGroup.setName(name);
        groupMapper.insert(newGroup);
        response.setCode(0);
        response.setMsg("add group success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> updateGroup(int id, String name, Group group) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group oldGroup = groupMapper.getOne(id);
        if (oldGroup == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        oldGroup.setName(name);
        groupMapper.update(oldGroup);
        response.setCode(0);
        response.setMsg("modify success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteGroup(int id) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group group = groupMapper.getOne(id);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        groupMapper.delete(id);
        response.setCode(0);
        response.setMsg("delete success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> addUserToGroup(int groupId, String userId) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group group = groupMapper.getOne(groupId);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        User user = userMapper.getOne(userId);
        if (user == null) {
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Auth auth = new Auth(userId, groupId, 0);
        authMapper.insert(auth);
        response.setCode(0);
        response.setMsg("add success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> addBatchUserToGroup(int groupId, MultipartFile multipartFile) {
       ResponseEntity responseEntity=authService.checkPermission("1-0");
       Response response=new Response();
       if(responseEntity!=null)
           return responseEntity;
       Group group=groupMapper.getOne(groupId);
       if(group==null){
           response.setCode(-1);
           return new ResponseEntity<>(response, HttpStatus.OK);
       }
        File file = new File("./"+multipartFile.getOriginalFilename());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            multipartFile.transferTo(file);
            String line;
            int judge=0;
            BufferedReader reader=new BufferedReader(new FileReader(file));
            List<AddResult> addResults=new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                AddResult addResult=new AddResult();
                addResult.setUserId(line);
                User user=userMapper.getOne(line);
                if(user!=null){
                    judge=-2;
                    addResult.setStatus(-1);
                    addResults.add(addResult);
                    continue;
                }
                Auth auth=authMapper.getAuthById(line,groupId);
                if(auth!=null){
                    judge=-2;
                    addResult.setStatus(-2);
                    addResults.add(addResult);
                    continue;
                }
                addResult.setStatus(0);
                Auth auth1=new Auth();
                auth1.setClassId(groupId);
                auth1.setPrivilege(0);
                auth1.setUserId(line);
                authMapper.insert(auth1);
                addResults.add(addResult);
            }
            response.setContent(addResults);
            response.setCode(judge);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (IOException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public ResponseEntity<?> deleteUserInGroup(int groupId, String memberId) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group group = groupMapper.getOne(groupId);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        User user = userMapper.getOne(memberId);
        if (user == null) {
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Auth auth = new Auth();
        auth.setClassId(groupId);
        auth.setUserId(memberId);
        auth.setPrivilege(0);
        authMapper.delete(auth);
        response.setCode(0);
        response.setMsg("delete success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> addAssistantToGroup(int groupId, String assistantId) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group group = groupMapper.getOne(groupId);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        User user = userMapper.getOne(assistantId);
        if (user == null) {
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Auth auth = new Auth();
        auth.setClassId(groupId);
        auth.setUserId(assistantId);
        auth.setPrivilege(1);
        authMapper.insert(auth);
        response.setCode(0);
        response.setMsg("add success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteAssistantInGroup(int groupId, String assistantId) {
        ResponseEntity responseEntity = authService.checkPermission("1-0");
        if (responseEntity != null) return responseEntity;
        Response response = new Response();
        Group group = groupMapper.getOne(groupId);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        User user = userMapper.getOne(assistantId);
        if (user == null) {
            response.setCode(-2);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Auth auth = new Auth();
        auth.setClassId(groupId);
        auth.setUserId(assistantId);
        auth.setPrivilege(1);
        authMapper.delete(auth);
        response.setCode(0);
        response.setMsg("delete success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getAssistantInGroup(int groupId) {
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        ResponseEntity responseEntity2 = authService.checkPermission("0-"+groupId);
        if (responseEntity1 != null ) return responseEntity1;
        if (responseEntity2 != null ) return responseEntity2;

        Response response = new Response();
        Group group = groupMapper.getOne(groupId);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        List<Auth> auths = authMapper.getClassAssistant(groupId);
        if(auths.size()==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        for(int i =0;i<auths.size();i++){
            userMapper.getOne(auths.get(i).getUserId());
            response.setContent(auths.get(i));
        }
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getUsersInGroup(int groupId) {
        ResponseEntity responseEntity1 = authService.checkPermission("1-0");
        ResponseEntity responseEntity2 = authService.checkPermission("0-"+groupId);
        if (responseEntity1 != null ) return responseEntity1;
        if (responseEntity2 != null ) return responseEntity2;

        Response response = new Response();
        Group group = groupMapper.getOne(groupId);
        if (group == null) {
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        List<Auth> auths = authMapper.getClassMembers(groupId);
        if(auths.size()==0){
            response.setCode(-1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        for(int i =0;i<auths.size();i++){
            userMapper.getOne(auths.get(i).getUserId());
            response.setContent(auths.get(i));
        }
        response.setCode(0);
        response.setMsg("get success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
