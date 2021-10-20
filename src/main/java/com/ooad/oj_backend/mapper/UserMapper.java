package com.ooad.oj_backend.mapper;

import com.ooad.oj_backend.mybatis.entity.User;
import com.ooad.oj_backend.mybatis.entity.UserView;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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

    @Select("        SELECT\n" +
            "        id, name,mail\n" +
            "        FROM User\n" +
            "        order by id limit #{itemsPerPage} offset #{offset} ")
    List<UserView> getAllByPage(@Param("offset")int offset,@Param("itemsPerPage") int itemsPerPage);

    @Insert("       INSERT INTO\n" +
            "         User\n" +
            "       ( id, name,mail,passWord)\n" +
            "       VALUES\n" +
            "       (#{id}, #{name}, #{mail},#{password})")
    void insert(User user);

    @Update("       UPDATE\n" +
            "        User\n" +
            "       SET \n" +
            "       id = #{id}," +
            "       name = #{name}," +
            " password=#{password},\n" +
            "       mail = #{mail}\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void update(User user);

    @Update("       UPDATE\n" +
            "        User\n" +
            "       SET \n" +
            "       id = #{id}," +
            "       name = #{name}," +
            " \n" +
            "       mail = #{mail}\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void updateWithoutPassWord(User user);

    @Delete("       DELETE FROM\n" +
            "            User\n" +
            "       WHERE \n" +
            "       id =#{id}")
    void delete(String id);
}
