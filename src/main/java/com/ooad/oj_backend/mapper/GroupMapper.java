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
            "FROM class where id!=0 order by id limit #{itemsPerPage} offset #{offset}")
    List<Group> getAll(@Param("offset")int offset,@Param("itemsPerPage") int itemsPerPage);
    @Select("       SELECT\n" +
            "        count(*)\n" +
            "FROM class where id!=0 order by id ")
    int getAllNumber();

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
            "        where name like '%${search}%' and id!=0 limit #{itemsPerPage} offset #{offset}")
    List<Group>  searchClass(@Param("search") String search,@Param("offset")int offset,@Param("itemsPerPage") int itemsPerPage);

    @Select("        SELECT\n" +
            "       count(*)\n" +
            "        FROM class\n" +
            "        where name and id!=0 like '%${search}%' ")
    int  searchClassNumber(@Param("search") String search);

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
            "       name = #{name}\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void update(Group group);

    @Delete("       DELETE FROM\n" +
            "            class\n" +
            "       WHERE \n" +
            "       id =#{id} and id!=0")
    void delete(int id);

    @Delete("       DELETE FROM\n" +
            "            auth\n" +
            "       WHERE \n" +
            "       classId=#{id}")
    void deleteAuth(int id);

    @Select("        SELECT\n" +
            "       count(*)\n" +
            "        FROM auth\n" +
            "        where classId =#{classId}  and privilege =0")
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
