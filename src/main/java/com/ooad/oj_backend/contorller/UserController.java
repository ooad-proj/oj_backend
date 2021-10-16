package com.ooad.oj_backend.contorller;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import com.ooad.oj_backend.service.UserService;
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
    private UserService userService;

    @PostMapping ("user")
    @ResponseBody
    public ResponseEntity<?> addUser(String id,@RequestParam(value = "name",required = false)String name,String passWord,@RequestParam(value = "name",required = false) String mail) {
        return userService.addUser(id,name,passWord,mail);
    }
    @RequestMapping(value = "user/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }
    @RequestMapping(value = "user/{id}",method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable String id,@RequestBody User user) {
        return userService.updateUser(id,user);
    }
    @RequestMapping(value = "user/details/{id}",method = RequestMethod.GET)
    public ResponseEntity<?> getUserInformation(@PathVariable String id) {
       return userService.getUserInformation(id);
    }
    @RequestMapping(value = "user/all",method = RequestMethod.GET)
    public ResponseEntity<?> getUsersInformation() {
       return userService.getUsersInformation();
    }
}