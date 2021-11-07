package com.ooad.oj_backend.controller.user;

import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/")
public class AuthController {
    @Autowired
    private AuthService authService;
    @ResponseBody
    @PostMapping(value = "auth/login")
    public ResponseEntity<?> Login(String id, String password) {
       return authService.Login(id,password);
    }
    @ResponseBody
    @PostMapping("auth/logout")
    public Response Logout() {
        return authService.Logout();
    }
    @RequestMapping(value = "auth/info",method = RequestMethod.PUT)
    public ResponseEntity<?> changeSelfInformation(String name,String mail) {
        return authService.changeSelfInformation(name,mail);
    }
    @RequestMapping(value = "auth/role",method = RequestMethod.GET)
    public ResponseEntity<?> checkRole() {
        return authService.checkRole();
    }
    @RequestMapping(value = "auth/info",method = RequestMethod.GET)
    public ResponseEntity<?> getSelfInformation() {
        return authService.getSelfInformation();
    }
    @RequestMapping(value = "auth/groups",method = RequestMethod.GET)
    public ResponseEntity<?> getGroupInformation() {
        return authService.getGroupInformation();
    }
    @RequestMapping(value = "auth/password",method = RequestMethod.PUT)
    public ResponseEntity<?> changePassWord(String oldPassword,String newPassword) {
        return authService.changePassWord(oldPassword, newPassword);
    }
    /*@RequestMapping(value = "auth/isLogin",method = RequestMethod.PUT)
    public Response isLogin() {
        return authService.isLogin();
    }*/
    @RequestMapping(value = "auth/isLogin",method = RequestMethod.GET)
    public HashMap<String, Boolean> isLogin() {
        return authService.isLogin();
    }
    @RequestMapping(value = "auth/groups/at",method = RequestMethod.GET)
    public ResponseEntity<?> getUserGroup(int page,int itemsPerPage,String search) {
        return authService.getUserGroup(page, itemsPerPage, search);
    }
}
