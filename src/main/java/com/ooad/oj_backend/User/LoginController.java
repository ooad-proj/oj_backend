package com.ooad.oj_backend.User;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.ooad.oj_backend.mapper.UserMapper;
import com.ooad.oj_backend.mybatis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录测试 
 * @author kong
 *
 */
@RestController
@RequestMapping("/api/")
public class LoginController {
    @Autowired
    private UserMapper userMapper;

    @RequestMapping(value = "user",method = RequestMethod.POST)
    @ResponseBody
    public void addUser(String id,String name,String password,String mail) {
        User user=new User();
        user.setId(id);
        user.setPassword(password);
        if(name!=null){
            user.setName(name);
        }
        if(mail!=null){
            user.setMail(mail);
        }
        userMapper.insert(user);
    }
    @RequestMapping(value = "user",method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(String id) {
        userMapper.delete(id);
    }
    /*@RequestMapping(value = "user",method = RequestMethod.PUT)
    @ResponseBody
    public void updateUser(String id) {
        userMapper.update();
    }*/

    // 测试登录  ---- http://localhost:8081/api/auth/login?id=1&&password=1
    @RequestMapping(value = "auth/login",method = RequestMethod.POST)
    @ResponseBody
    public SaResult Login(String id,  String password) {
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