package com.ooad.oj_backend.mapper.record;

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
<<<<<<< Updated upstream

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
=======
    @Select("select resultId,submitTime,score,allowPartial from(select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "       count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "       substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "           if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "       @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "    temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            "    score where userId like '%${userId}%' and problemId like '%${problemId}%' and stateCode like '%${stateCode}%'" +
            "limit #{itemsPerPage} offset #{offset}")
    List<Result> getResult(@Param("userId") String userId,@Param("problemId") int problemId,@Param("stateCode") String stateCode,@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage);

    @Select("select count(*) from(select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "       count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "       substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "           if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "       @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "    temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            " score where userId like '%${userId}%' and problemId like '%${problemId}%' and stateCode like '%${stateCode}%'")
    int getResultNum(@Param("userId") String userId,@Param("problemId") int problemId,@Param("stateCode") String stateCode);

    @Select("select userId,problemId,submitTime,\n" +
            "       count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "       substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "           if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "       @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId\n" +
            "from(select punishRule,submitTime,p.problemId,userId,totalScore,resultId\n" +
            "from result " +
            "join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)temp " +
            "join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q " +
            "group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime;")
    List<Result>get();

>>>>>>> Stashed changes
}
