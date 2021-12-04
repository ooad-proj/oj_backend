package com.ooad.oj_backend.mapper.record;

import com.ooad.oj_backend.mybatis.entity.Rank;
import com.ooad.oj_backend.mybatis.entity.Result;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RecordMapper {
    @Insert("       INSERT INTO\n" +
            "         result (resultId,submitTime,userId,problemId)\n" +
            "       VALUE\n" +
            "       (#{resultId},#{submitTime},#{userId},#{problemId})")
    void addResult(Result result);

    @Insert("       INSERT INTO\n" +
            "         checkpoint (id,total,correct,timeCost,memoryCost,code,name,message,color,resultId)\n" +
            "       VALUE\n" +
            "       (#{r.id},#{r.total},#{r.correct},#{r.timeCost},#{r.memoryCost},#{r.code},#{r.name},#{r.message},#{r.color},#{resultId})")
    void addCheckpoint(@Param("r") com.ooad.oj_backend.rabbitmq.entity.Result result,@Param("resultId")String resultId);

    @Select("select * from checkpoint " +
            "where resultId = #{submitId}")
    List<com.ooad.oj_backend.rabbitmq.entity.Result> getCheckpoint(String submitId);
    @Select("        SELECT\n" +
            "        submitTime\n" +
            "        FROM result\n" +
            "        where userId=#{userId} and submitTime>#{milliSecond}" +
            "        order by submitTime asc")
    List<Long> getSubmitNum(String userId,long milliSecond);

    @Select("        SELECT\n" +
            "        submitTime\n" +
            "        FROM result\n" +
            "        where submitTime>#{milliSecond}" +
            "        order by submitTime asc")
    List<Long> getAllSubmitNum(String userId,long milliSecond);
    @Select("select r.resultId,r.submitTime,(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score from result r join (select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "       count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "       substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "           if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "       @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "    temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            "    score on score.resultId=r.resultId where r.userId like '%${userId}%' and r.problemId like '%${problemId}%' and stateCode like '%${stateCode}%'" +
            "order by submitTime desc" +
            "limit #{itemsPerPage} offset #{offset}")
    List<Result> getResult(@Param("userId") String userId,@Param("problemId") String problemId,@Param("stateCode") String stateCode,@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage);

    @Select("select count(*) from result join (select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "       count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "       substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "           if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "       @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "    temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            "    score on score.resultId=result.resultId where result.userId like '%${userId}%' and result.problemId like '%${problemId}%' and stateCode like '%${stateCode}%'")
    int getResultNum(@Param("userId") String userId,@Param("problemId") String problemId,@Param("stateCode") String stateCode);

    /*@Select("select u1.id as userId,name as userName,correctNum,answerNum,correctNum/answerNum as correctRate,(if(@pr=correctNum,@r,@r:=@r+1))as rank,@pr:=correctNum as i from User u1 join (select u.id,sum(correct)as correctNum,sum(answerNum) as answerNum from User u join (select id,name,problemId,if(count(if(stateCode='AC',1,null))>0,1,0)as correct,if((count(*))>0,1,0) as answerNum  from User u join (select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "                   count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "                   substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "                       if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "                   @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "            from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "            from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "                temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            "                score on score.userId=u.id group by problemId,id)up on up.id=u.id group by id)u2 on u1.id=u2.id ,(select @r:=0,@pr:=null)q order by correctNum desc " +
            "limit #{itemsPerPage} offset #{offset};")
    List<Rank>getRank(@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage);*/
    @Select("select u1.id,name,correctNum,answerNum,correctNum/answerNum as correctRate,(if(@pr=correctNum,@r,@r:=@r+1))as rank,@pr:=correctNum from User u1 join (select u.id,sum(correct)as correctNum,sum(answerNum) as answerNum from User u join (select id,name,count(if(stateCode='AC',1,null))as correct,count(*) as answerNum  from User u join (select temp.resultId,userId,submitTime,allowPartial,\n" +
            "                   count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "                   substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "                       if((@preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "                   @preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "            from(select resultId,punishRule,submitTime,userId,totalScore,allowPartial\n" +
            "            from result join problem p on result.problemId = p.problemId order by userId,submitTime)\n" +
            "                temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,submitTime,temp.resultId order by userId,submitTime)\n" +
            "                score on score.userId=u.id group by id)up on up.id=u.id group by id)u2 on u1.id=u2.id ,(select @r:=0,@pr:=null)q order by correctNum desc limit #{itemsPerPage} offset #{offset};")
    List<Rank>getRank(@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage);

    @Select("select count(*) from User u1 join (select u.id,sum(correct)as correctNum,sum(answerNum) as answerNum from User u join (select id,name,problemId,if(count(if(stateCode='AC',1,null))>0,1,0)as correct,if((count(*))>0,1,0) as answerNum  from User u join (select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "                   count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "                   substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "                       if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "                   @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "            from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "            from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "                temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            "                score on score.userId=u.id group by problemId,id)up on up.id=u.id group by id)u2 on u1.id=u2.id ,(select @r:=0,@pr:=null)q order by correctNum desc " +
            "limit #{itemsPerPage} offset #{offset};")
    int getRankNum();
    @Select("select u1.id,name,correctNum,answerNum,correctNum/answerNum as correctRate,(if(@pr=correctNum,@r,@r:=@r+1))as rank,@pr:=correctNum from User u1 join (select u.id,sum(correct)as correctNum,sum(answerNum) as answerNum from User u join (select id,name,count(if(stateCode='AC',1,null))as correct,count(*) as answerNum  from User u join (select temp.resultId,userId,submitTime,allowPartial,\n" +
            "                   count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "                   substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "                       if((@preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "                   @preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "            from(select resultId,punishRule,submitTime,userId,totalScore,allowPartial\n" +
            "            from result join problem p on result.problemId = p.problemId order by userId,submitTime)\n" +
            "                temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,submitTime,temp.resultId order by userId,submitTime)\n" +
            "                score on score.userId=u.id group by id)up on up.id=u.id group by id)u2 on u1.id=u2.id ,(select @r:=0,@pr:=null)q where u2.id=#{userId} order by correctNum desc")
    List<Rank>getUserRank(@Param("userId")String userId);

}
