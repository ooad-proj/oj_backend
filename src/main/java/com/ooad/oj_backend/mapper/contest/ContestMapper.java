package com.ooad.oj_backend.mapper.contest;

import com.ooad.oj_backend.mybatis.entity.Auth;
import com.ooad.oj_backend.mybatis.entity.Contest;
import com.ooad.oj_backend.mybatis.entity.Group;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ContestMapper {

    @Select("        SELECT\n" +
            "        title, description,startTime,endTime,access\n" +
            "        FROM contest\n" +
            "        where id=#{contestId}")
    Contest getOneContest(int contestId);

    @Select("select classId from contest where contest.id=#{contestId}")
    int getClassByContest(int contestId);

    @Select("        SELECT\n" +
            "        id,description,title,startTime,endTime,access\n" +
            "        FROM contest\n" +
            "        where classId=#{groupId} and title like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
    List<Contest> getContestInGroup(@Param("groupId")int groupId ,@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage, @Param("search") String search);

    @Select("        SELECT\n" +
            "        count(*)" +
            "        FROM contest\n" +
            "        where classId=#{groupId}")
    int getContestInGroupNum(int groupId );

    @Select("       SELECT\n" +
            "        id, classId, startTime, endTime, title ,description, creatorId,access\n" +
            "FROM contest where title like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
    List<Contest> getAllContest(@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage,@Param("search") String search);

    @Select("       SELECT\n" +
            "     count(*)\n" +
            "FROM contest where title like '%${search}%' ")
    int getTotalNum(@Param("search") String search);

    @Select("       SELECT\n" +
            "        count(*)\n" +
            "from  contest join auth a on contest.classId = a.classId  where a.UserId =#{userId} and a.privilege=1 and title like '%${search}%' ")
    int getManagementNumber( @Param("userId") String userId, @Param("search") String search);

    @Select("       SELECT\n" +
            "        id, a.classId, startTime, endTime, title ,description, creatorId,access\n" +
            "from  contest join auth a on contest.classId = a.classId  where a.UserId =#{userId} and a.privilege=1 and title like '%${search}%' ")
    List<Contest> getManagementContest( @Param("userId") String userId, @Param("search") String search);

    @Insert("       INSERT INTO\n" +
            "         contest\n" +
            "       (id, classId, startTime, endTime, title ,description, creatorId,access)\n" +
            "       VALUES\n" +
            "       (#{id},#{classId},#{startTime},#{endTime},#{title},#{description},#{creatorId},#{access} )")
    void insert(Contest contest);

    @Update("       UPDATE\n" +
            "        contest\n" +
            "       SET \n" +
            "       title = #{title}," +
            "       description = #{description}," +
            "       startTime = #{startTime}," +
            "       endTime = #{endTime},access=#{access}\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void update(Contest contest);

    @Delete("       DELETE FROM\n" +
            "            contest\n" +
            "       WHERE \n" +
            "       id =#{id}")
    void delete(int contestId);

    @Select("       select id," +
            "title," +
            "endTime " +
            "from contest where endTime>#{nowTime}")
    List<Contest> getCloseContest( @Param("nowTime") long nowTime);

    @Select("select * from contest where classId in (" +
            "    select id from class join auth a on class.id = a.classId where a.UserId = userId) and access=1;")
    List<Contest> getAllowedContest( @Param("userId") String userId);
}
