package com.ooad.oj_backend.contorller;
import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录测试 
 * @author kong
 *
 */
@RestController
@RequestMapping("/api/")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @PostMapping ("user")
    @ResponseBody
    public void addUser(String id,String name,String passWord,String mail) {
        User user=new User();
        user.setId(id);
        user.setPassWord(passWord);
        if(name!=null){
            user.setName(name);
        }
        if(mail!=null){
            user.setMail(mail);
        }
        userMapper.insert(user);
    }
    @RequestMapping(value = "user/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(@PathVariable String id) {
        userMapper.delete(id);
    }
    @RequestMapping(value = "user/{id}",method = RequestMethod.PUT)
    public void updateUser(@PathVariable String id,@RequestBody User user) {
        userMapper.update(user);
    }
    @RequestMapping(value = "user/details/{id}",method = RequestMethod.GET)
    public void getUserInformation(@PathVariable String id) {
        User user=userMapper.getOne(id);
    }
    @RequestMapping(value = "user/all",method = RequestMethod.GET)
    public List<User> getUsersInformation() {
        List<User> users=userMapper.getAll();
        return users;
    }
    @RequestMapping(value = "user/all",method = RequestMethod.GET)
    public ResponseEntity<?> test() {
        StpUtil.getLoginId();
        ResponseEntity response=new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<User> users=userMapper.getAll();
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    // 测试登录  ---- http://localhost:8081/api/auth/login?id=1&&password=1
}