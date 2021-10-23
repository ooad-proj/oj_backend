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

    @Select("        SELECT\n" +
            "        id, name\n" +
            "        FROM class\n" +
            "        where name=#{name}")
    Group getOneByName(String name);

    @Select("        SELECT\n" +
            "        id, name\n" +
            "        FROM class\n" +
            "        where name like '%${search}%'")
    List<Group>  searchClass(String search);

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

    @Select("        SELECT\n" +
            "       count(*)\n" +
            "        FROM auth\n" +
            "        where classId =#{classId}")
    int getMemberNumber(int classId);


    @Select("        SELECT\n" +
            "       count(*)\n" +
            "        FROM auth\n" +
            "        where classId =#{classId} and privilege =1 ")
    int getAssistantNumber(int classId);

//    @Delete("       DELETE FROM\n" +
//            "            class\n" +
//            "       WHERE \n" +
//            "       id =#{id}")
//    void deleteUserInGroup(String groupId,String userID);
}
