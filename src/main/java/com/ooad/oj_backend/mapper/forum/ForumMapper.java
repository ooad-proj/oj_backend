package com.ooad.oj_backend.mapper.forum;

import com.ooad.oj_backend.mybatis.entity.Announcement;
import com.ooad.oj_backend.mybatis.entity.CommentByPage;
import com.ooad.oj_backend.mybatis.entity.PostByPage;
import com.ooad.oj_backend.mybatis.entity.PostInformation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ForumMapper {

    @Insert("insert into post (postId,groupId,userId, title, content,modifyTime,goPublic,goMail) " +
            "values ( 0,#{groupId},#{userId},#{title},#{content},#{modifyTime},#{goPublic},#{goMail});")
    void createPost(@Param("groupId")int groupId,@Param("userId")String userId, @Param("title")String title,@Param("content")String content,@Param("modifyTime")long modifyTime, @Param("goPublic")Boolean goPublic,@Param("goMail")Boolean goMail);

    @Update("       UPDATE\n" +
            "        post\n" +
            "       SET \n" +
            "       title = #{title}," +
            "       content = #{content}," +
            "       goPublic = #{goPublic}," +
            "       modifyTime = #{modifyTime}" +
            "       WHERE \n" +
            "       postId = #{postId}")
    void updatePost(@Param("postId")int postId,@Param("title")String title,@Param("content")String content, @Param("goPublic")Boolean goPublic,@Param("modifyTime") long modifyTime);

    @Select("select groupId from post where postId = #{postId};")
    int getGroupId(int postId);

    @Select("select userId from post where postId = #{postId};")
    String getCreatorId(int postId);

    @Select("select goPublic from post where postId = #{postId};")
    Boolean getGoPublic(int postId);

    @Delete("       DELETE FROM\n" +
            "            post\n" +
            "       WHERE \n" +
            "       postId =#{postId}")
    void delete(int postId);

    @Select("SELECT postId,title,content as preview,userId,u.name as userName,modifyTime FROM post join User u on post.userId = u.id " +
            "        where groupId =#{groupId} and title like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
    List<PostByPage> getPostByPage(@Param("groupId") int groupId, @Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage, @Param("search") String search );

    @Select("SELECT count(*) FROM post join User u on post.userId = u.id " +
            "        where groupId =#{groupId} and title like '%${search}%' ")
    int getPostByPageTotalAmount(@Param("groupId") int groupId, @Param("search") String search );

    @Select("SELECT title,content,userId,u.name,modifyTime,goPublic,groupId FROM post join User u on post.userId = u.id " +
            "        where postId =#{postId}")
    PostInformation getPostInformation(@Param("postId") int postId );

    @Insert("insert into comment (commentId, postId, floorId, userId, comment, modifyTime) " +
            "values (0,#{postId},#{floorId},#{userId},#{comment},#{modifyTime})")
    void addComment(@Param("postId")int postId, @Param("floorId")int floorId, @Param("userId")String userId, @Param("comment")String comment, @Param("modifyTime")long modifyTime);

    @Insert("select COUNT(*) from comment where postId=#{postId}")
    int getFloorNum(int postId);

    @Select("select postId from comment where commentId = #{commentId};")
    int getPostId(int commentId);

    @Select("select userId from comment where commentId = #{commentId};")
    String getCommentCreator(int commentId);

    @Delete("       DELETE FROM\n" +
            "            comment\n" +
            "       WHERE \n" +
            "       commentId =#{commentId}")
    void deleteComment(int commentId);

    @Select("SELECT commentId,floorId,userId,u.name as userName ,comment,modifyTime FROM comment join User u on comment.userId = u.id " +

            "        where postId =#{postId} "+
            "        order by id limit #{itemsPerPage} offset #{offset}" )
    List<CommentByPage> getCommentByPage(@Param("postId") int postId, @Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage );

    @Select("SELECT count(*) FROM comment join User u on comment.userId = u.id " +
            "        where postId =#{postId}")
    int getCommentByPageTotalAmount(@Param("postId") int postId);

    @Select("SELECT postId,title,content,userId,u.name,modifyTime FROM post join User u on post.userId = u.id " +
            " where groupId =#{groupId} and goPublic=TRUE" +
            "        order by modifyTime desc limit #{length}" )
    List<Announcement> getAnnouncement(@Param("groupId") int groupId,@Param("length") int length );










//    @Select("        SELECT\n" +
//            "        title, description,startTime,endTime\n" +
//            "        FROM contest\n" +
//            "        where id=#{contestId}")
//    Contest getOneContest(int contestId);
//
//    @Select("select classId from contest where contest.id=#{contestId}")
//    int getClassByContest(int contestId);
//
//    @Select("        SELECT\n" +
//            "        id,description,title,startTime,endTime\n" +
//            "        FROM contest\n" +
//            "        where classId=#{groupId} and title like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
//    List<Contest> getContestInGroup(@Param("groupId")int groupId ,@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage, @Param("search") String search);
//
//    @Select("        SELECT\n" +
//            "        count(*)" +
//            "        FROM contest\n" +
//            "        where classId=#{groupId}")
//    int getContestInGroupNum(int groupId );
//
//    @Select("       SELECT\n" +
//            "        id, classId, startTime, endTime, title ,description, creatorId\n" +
//            "FROM contest where title like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
//    List<Contest> getAllContest(@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage,@Param("search") String search);
//
//    @Select("       SELECT\n" +
//            "     count(*)\n" +
//            "FROM contest where title like '%${search}%' ")
//    int getTotalNum(@Param("search") String search);
//
//    @Select("       SELECT\n" +
//            "        count(*)\n" +
//            "from  contest join auth a on contest.classId = a.classId  where a.UserId =#{userId} and a.privilege=1 and title like '%${search}%' ")
//    int getManagementNumber( @Param("userId") String userId, @Param("search") String search);
//
//    @Select("       SELECT\n" +
//            "        id, a.classId, startTime, endTime, title ,description, creatorId\n" +
//            "from  contest join auth a on contest.classId = a.classId  where a.UserId =#{userId} and a.privilege=1 and title like '%${search}%' ")
//    List<Contest> getManagementContest( @Param("userId") String userId, @Param("search") String search);
//
//    @Insert("       INSERT INTO\n" +
//            "         contest\n" +
//            "       (id, classId, startTime, endTime, title ,description, creatorId)\n" +
//            "       VALUES\n" +
//            "       (#{id},#{classId},#{startTime},#{endTime},#{title},#{description},#{creatorId} )")
//    void insert(Contest contest);
//
//    @Update("       UPDATE\n" +
//            "        contest\n" +
//            "       SET \n" +
//            "       title = #{title}," +
//            "       description = #{description}," +
//            "       startTime = #{startTime}," +
//            "       endTime = #{endTime}\n" +
//            "       WHERE \n" +
//            "       id = #{id}")
//    void update(Contest contest);
//
//    @Delete("       DELETE FROM\n" +
//            "            contest\n" +
//            "       WHERE \n" +
//            "       id =#{id}")
//    void delete(int contestId);
//
//    @Select("       select id," +
//            "title," +
//            "endTime " +
//            "from contest where endTime>#{nowTime}")
//    List<Contest> getCloseContest( @Param("nowTime") long nowTime);
//
//    @Select("select * from contest where classId in (" +
//            "    select id from class join auth a on class.id = a.classId where a.UserId = userId );")
//    List<Contest> getAllowedContest( @Param("userId") String userId);

}
