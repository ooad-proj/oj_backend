package com.ooad.oj_backend.contorller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class authController {
    UserMapper userMapper;
    @ResponseBody
    @PostMapping("auth/login")
    public SaResult Login(String id, String password) {
        User user=userMapper.getOne(id);
        int i=0;
        if(user.getId().equals(id) && user.getPassword().equals(password)) {
            StpUtil.login(id);
            return SaResult.ok("登录成功");
        }
        return SaResult.error("登录失败");
    }

    // 查询登录状态  ---- http://localhost:8081/acc/isLogin
    @RequestMapping("isLogin")
    public SaResult isLogin() {
        StpUtil.getLoginId();
        return SaResult.ok("是否登录：" + StpUtil.isLogin());
    }

    // 测试注销  ---- http://localhost:8081/acc/logout
    @RequestMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }
}
