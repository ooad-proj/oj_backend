package com.ooad.oj_backend.mapper;

import com.ooad.oj_backend.mybatis.entity.Auth;
import com.ooad.oj_backend.mybatis.entity.RoleView;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AuthMapper {
//    @Select("       SELECT\n" +
//            "        id, name\n" +
//            "FROM auth order by id")
//    List<Auth> getAll();

    @Select("        SELECT\n" +
            "        userId, classId,privilege\n" +
            "        FROM auth\n" +
            "        where userId=#{userId}")
    List <Auth> getOne(String userId);



    @Select("        SELECT\n" +
            "        userId, classId,privilege\n" +
            "        FROM auth\n" +
            "        where userId=#{userId} and classId=#{groupId}")
    Auth getAuthById(@Param("userId")String userId,@Param("groupId")int groupId);

    @Select("        SELECT\n" +
            "        userId, classId,privilege\n" +
            "        FROM auth\n" +
            "        where userId like '%${userId}%' and classId=#{groupId} limit #{itemsPerPage} offset #{offset}")
    List<Auth> getOneAuth(@Param("userId")String userId, @Param("groupId")int groupId,@Param("offset")int offset,@Param("itemsPerPage") int itemsPerPage);

    @Select("        SELECT\n" +
            "        count(*)\n" +
            "        FROM auth\n" +
            "        where userId like '%${userId}%' and classId=#{groupId} ")
    int getNumber(@Param("userId")String userId, @Param("groupId")int groupId);

    @Select("SELECT classId as groupId, c.name as groupName,(case (max(privilege)) " +
            "when 1 then 'assistant' when 0 then 'student' end)as role " +
            "FROM auth join class c on c.id = auth.classId where userId=#{userId} group by classId")
    List<RoleView> getAuthListById(String userId);

    @Select("        SELECT\n" +
            "        UserId,classId,privilege\n" +
            "        FROM auth\n" +
            "        where classId=#{classId} and privilege=1")
    List <Auth> getClassAssistant(int classId);

    @Select("        SELECT\n" +
            "        UserId,classId,privilege\n" +
            "        FROM auth\n" +
            "        where classId=#{classId} and privilege=0 limit #{itemsPerPage} offset #{offset}")
    List <Auth> getClassMembers(@Param("classId")int classId,@Param("offset")int offset,@Param("itemsPerPage")int itemsPerPage);

    @Select("        SELECT\n" +
            "        count(*)\n" +
            "        FROM auth\n" +
            "        where classId=#{classId} and privilege=0 ")
    int getClassNumber(@Param("classId")int classId);

    @Insert("       INSERT INTO\n" +
            "         auth\n" +
            "       (UserId, classId,privilege)\n" +
            "       VALUES\n" +
            "       (#{userId},#{classId},#{privilege})")
    void insert(Auth auth);

//    @Update("       UPDATE\n" +
//            "        auth\n" +
//            "       SET \n" +
//            "       userId = #{userId}," +
//            "       classId = #{classId}," +
//            "       privilege = #{privilege}," +
//            "       WHERE \n" +
//            "       classId = #{classId}")
//    void update(Auth auth);

    @Delete("       DELETE FROM\n" +
            "            auth\n" +
            "       WHERE \n" +
            "       UserId =#{UserId} and classId=#{classId} and privilege =#{privilege} ")
    void delete(Auth auth);
}
