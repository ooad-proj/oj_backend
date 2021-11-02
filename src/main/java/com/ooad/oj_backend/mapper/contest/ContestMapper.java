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
            "        title, description,startTime,endTime\n" +
            "        FROM contest\n" +
            "        where contestId=#{contestId}")
    Contest getOneContest(int contestId);

    @Select("        SELECT\n" +
            "        id,description,title,startTime,endTime\n" +
            "        FROM contest\n" +
            "        where classId=#{groupId}  order by id limit #{itemsPerPage} offset #{offset}")
    List<Contest> getContestInGroup(int groupId ,@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage);

    @Select("        SELECT\n" +
            "        count(*)" +
            "        FROM contest\n" +
            "        where classId=#{groupId}")
    int getContestInGroupNum(int groupId );

    @Select("       SELECT\n" +
            "        id, classId, startTime, endTime, title ,description, creatorId\n" +
            "FROM contest where id like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
    List<Contest> getAllContest(@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage,@Param("search") String search);

    @Select("       SELECT\n" +
            "     count(*)\n" +
            "FROM contest where title like '%${search}%' ")
    int getTotalNum(@Param("search") String search);

    @Select("       SELECT\n" +
            "        id, classId, startTime, endTime, title ,description, creatorId\n" +
            "from  contest join auth a on contest.classId = a.classId  where a.UserId =#{userId} and a.privilege=1 and title like '%${search}%' ")
    List<Contest> getManagementContest( @Param("userId") String userId, @Param("search") String search);

    @Insert("       INSERT INTO\n" +
            "         contest\n" +
            "       (id, classId, startTime, endTime, title ,description, creatorId)\n" +
            "       VALUES\n" +
            "       (#{id},#{classId},#{startTime},#{endTime},#{title},#{description},#{creatorId} )")
    void insert(Contest contest);

    @Update("       UPDATE\n" +
            "        contest\n" +
            "       SET \n" +
            "       title = #{title}," +
            "       description = #{description}," +
            "       startTime = #{startTime}," +
            "       endTime = #{endTime}\n" +
            "       WHERE \n" +
            "       id = #{id}")
    void update(Contest contest);

    @Delete("       DELETE FROM\n" +
            "            contest\n" +
            "       WHERE \n" +
            "       id =#{id}")
    void delete(int contestId);
}
