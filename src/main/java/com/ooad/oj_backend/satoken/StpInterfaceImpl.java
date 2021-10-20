package com.ooad.oj_backend.satoken;
import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.stp.StpUtil;
import com.ooad.oj_backend.mapper.AuthMapper;
import com.ooad.oj_backend.mybatis.entity.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.dev33.satoken.stp.StpInterface;

/**
 * 自定义权限验证接口扩展
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private  AuthMapper authMapper;
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
        List <Auth>authList=authMapper.getOne((String) loginId);
        for (Auth auth:authList) {
            list.add((auth.getPrivilege()+"-"+auth.getClassId()));
        }
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        ArrayList<String>list=new ArrayList<>();
        List<String> permissionList=getPermissionList(loginId,loginType);
        boolean isUser=true;
        if(permissionList.size()==0){
            list.add("user");
            return list;
        }
        for (String permission:permissionList){
            if(permission.equals("1-0")){
                list.add("admin");
                isUser=false;
                break;
            }
            if(permission.startsWith("1")){
                list.add("assistant");
                isUser=false;
                break;
            }
        }
        if(isUser){
            list.add("student");
        }
        return list;
    }

}