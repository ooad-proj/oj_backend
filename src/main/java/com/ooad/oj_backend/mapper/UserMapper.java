package com.ooad.oj_backend.mapper;

import com.ooad.oj_backend.mybatis.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("       SELECT\n" +
            "        id, name,mail\n" +
            "FROM user order by id")
    List<User> getAll();

    @Select("        SELECT\n" +
            "        id, name,password,mail\n" +
            "        FROM User\n" +
            "        where id=#{id}")
    User getOne(String id);

    @Insert("       INSERT INTO\n" +
            "         User\n" +
            "       ( id, name,mail)\n" +
            "       VALUES\n" +
            "       (#{id}, #{name}, #{mail})")
    void insert(User user);

    @Update("       UPDATE\n" +
            "        User\n" +
            "       SET \n" +
            "       id = #{id}," +
            "       name = #{time},\n" +
            "       mail = #{mail}\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void update(User user);

    @Delete("       DELETE FROM\n" +
            "            User\n" +
            "       WHERE \n" +
            "       id =#{id}")
    void delete(String id);
}
