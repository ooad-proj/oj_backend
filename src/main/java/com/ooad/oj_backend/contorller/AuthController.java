package com.ooad.oj_backend.contorller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import com.ooad.oj_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class AuthController {
    @Autowired
    private UserMapper userMapper;
    private AuthService authService;
    @ResponseBody
    @PostMapping("auth/login")
    public ResponseEntity<?> Login(String id, String passWord) {
       return authService.Login(id,passWord);
    }

    // 查询登录状态  ---- http://localhost:8081/acc/isLogin

}
