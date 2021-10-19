package com.ooad.oj_backend.mapper;

import com.ooad.oj_backend.mybatis.entity.Group;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GroupMapper {
    @Select("       SELECT\n" +
            "        id, name\n" +
            "FROM class order by id")
    List<Group> getAll();

    @Select("        SELECT\n" +
            "        id, name\n" +
            "        FROM class\n" +
            "        where id=#{id}")
    Group getOne(int id);

    @Insert("       INSERT INTO\n" +
            "         class\n" +
            "       (name)\n" +
            "       VALUES\n" +
            "       (#{name})")
    void insert(Group group);

    @Update("       UPDATE\n" +
            "        class\n" +
            "       SET \n" +
            "       id = #{id}," +
            "       name = #{name},\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void update(Group group);

    @Delete("       DELETE FROM\n" +
            "            class\n" +
            "       WHERE \n" +
            "       id =#{id}")
    void delete(int id);

//    @Delete("       DELETE FROM\n" +
//            "            class\n" +
//            "       WHERE \n" +
//            "       id =#{id}")
//    void deleteUserInGroup(String groupId,String userID);
}
