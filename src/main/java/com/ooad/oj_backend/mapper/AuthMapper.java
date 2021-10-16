package com.ooad.oj_backend.mapper;

import com.ooad.oj_backend.mybatis.entity.Auth;
import com.ooad.oj_backend.mybatis.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AuthMapper {


    @Select("        SELECT\n" +
            "        userId,classId, privilege\n" +
            "        FROM auth\n" +
            "        where UserId=#{userId}")
    List<Auth> getOne(String userId);


}
