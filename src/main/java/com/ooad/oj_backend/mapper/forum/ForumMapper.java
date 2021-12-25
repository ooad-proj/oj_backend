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
    int getGroupId(@Param("postId")int postId);

    @Select("select userId from post where postId = #{postId};")
    String getPostCreatorId(@Param("postId")int postId);

    @Select("select userId from comment where commentId = #{commentId};")
    String getCommentCreatorId(@Param("commentId")int commentId);

    @Select("select goPublic from post where postId = #{postId};")
    Boolean getGoPublic(@Param("postId")int postId);

    @Delete("       DELETE FROM\n" +
            "            post\n" +
            "       WHERE \n" +
            "       postId =#{postId}")
    void delete(@Param("postId")int postId);

    @Select("SELECT postId,title,content as preview,userId,u.name as userName,modifyTime FROM post join User u on post.userId = u.id " +
            "        where groupId =#{groupId} and title like '%${search}%' order by id limit #{itemsPerPage} offset #{offset}")
    List<PostByPage> getPostByPage(@Param("groupId") int groupId, @Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage, @Param("search") String search );

    @Select("SELECT count(*) FROM post join User u on post.userId = u.id " +
            "        where groupId =#{groupId} and title like '%${search}%' ")
    int getPostByPageTotalAmount(@Param("groupId") int groupId, @Param("search") String search );

    @Select("SELECT title,content,userId,u.name as userName,modifyTime,goPublic,groupId FROM post join User u on post.userId = u.id " +
            "        where postId =#{postId}")
    PostInformation getPostInformation(@Param("postId") int postId );

    @Insert("insert into comment (commentId, postId, floorId, userId, comment, modifyTime) " +
            "values (0,#{postId},#{floorId},#{userId},#{comment},#{modifyTime})")
    void addComment(@Param("postId")int postId, @Param("floorId")int floorId, @Param("userId")String userId, @Param("comment")String comment, @Param("modifyTime")long modifyTime);

    @Select("select max(floorId) from comment where postId=#{postId}")
    String getFloorNum(@Param("postId")int postId);

    @Select("select postId from comment where commentId = #{commentId};")
    int getPostId(@Param("commentId")int commentId );

    @Select("select userId from comment where commentId = #{commentId};")
    String getCommentCreator(@Param("commentId")int commentId);

    @Delete("       DELETE FROM\n" +
            "            comment\n" +
            "       WHERE \n" +
            "       commentId =#{commentId}")
    void deleteComment(@Param("commentId")int commentId);

    @Select("SELECT commentId,floorId,userId,u.name as userName ,comment,modifyTime FROM comment join User u on comment.userId = u.id " +

            "        where postId =#{postId} "+
            "        order by floorId limit #{itemsPerPage} offset #{offset}" )
    List<CommentByPage> getCommentByPage(@Param("postId") int postId, @Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage );

    @Select("SELECT count(*) FROM comment join User u on comment.userId = u.id " +
            "        where postId =#{postId}")
    int getCommentByPageTotalAmount(@Param("postId") int postId);

    @Select("SELECT postId,title,content as preview,userId,u.name as userName ,modifyTime FROM post join User u on post.userId = u.id " +
            " where groupId =#{groupId} and goPublic=TRUE" +
            "        order by modifyTime desc limit #{length}" )
    List<Announcement> getAnnouncement(@Param("groupId") int groupId,@Param("length") int length );

}
