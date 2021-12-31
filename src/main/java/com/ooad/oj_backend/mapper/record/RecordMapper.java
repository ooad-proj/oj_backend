package com.ooad.oj_backend.mapper.record;

import com.ooad.oj_backend.mybatis.entity.Rank;
import com.ooad.oj_backend.mybatis.entity.Result;
import com.ooad.oj_backend.mybatis.entity.UserResult;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RecordMapper {
    @Insert("       INSERT INTO\n" +
            "         result (resultId,submitTime,userId,problemId,code)\n" +
            "       VALUE\n" +
            "       (#{resultId},#{submitTime},#{userId},#{problemId},#{code})")
    void addResult(Result result);

    @Insert("       INSERT INTO\n" +
            "         checkpoint (id,total,correct,timeCost,memoryCost,code,name,message,color,resultId)\n" +
            "       VALUE\n" +
            "       (#{r.id},#{r.total},#{r.correct},#{r.timeCost},#{r.memoryCost},#{r.code},#{r.name},#{r.message},#{r.color},#{resultId})")
    void addCheckpoint(@Param("r") com.ooad.oj_backend.rabbitmq.entity.Result result,@Param("resultId")String resultId);

    @Select("select * from checkpoint " +
            "where resultId = #{submitId}")
    List<com.ooad.oj_backend.rabbitmq.entity.Result> getCheckpoint(String submitId);
    @Select("select * from result " +
            "where resultId = #{submitId}")
    Result getResultAndCode(String submitId);
    @Select("        SELECT\n" +
            "        submitTime\n" +
            "        FROM result\n" +
            "        where userId=#{userId} and submitTime>#{milliSecond}" +
            "        order by submitTime asc")
    List<Long> getSubmitNum(@Param("userId") String userId,@Param("milliSecond")long milliSecond);

    @Select("        SELECT\n" +
            "        submitTime\n" +
            "        FROM result\n" +
            "        where submitTime>#{milliSecond}" +
            "        order by submitTime asc")
    List<Long> getAllSubmitNum(long milliSecond);

 /*   @Select("select r.userId,r.resultId,r.problemId,r.submitTime,stateCode,(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score from result r join (select temp.resultId,userId,problemId,submitTime,allowPartial,\n" +
            "       count(if(correct=1,1,null))/count(*)*totalScore*\n" +
            "       substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
            "           if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
            "       @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "from(select resultId,punishRule,submitTime,p.problemId,userId,totalScore,allowPartial\n" +
            "from result join problem p on result.problemId = p.problemId order by userId,problemId,submitTime)\n" +
            "    temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
            "    score on score.resultId=r.resultId where r.userId like '%${userId}%' and r.problemId like '%${problemId}%' and stateCode like '%${stateCode}%'" +
            "order by submitTime desc " +
            "limit #{itemsPerPage} offset #{offset}")*/
 @Select("select r.userId,r.resultId,score.contestId,c.classId as groupId,r.problemId,r.submitTime,stateCode,(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score from result r join\n" +
         "    (select temp.resultId,userId,contestId,problemId,submitTime,allowPartial,\n" +
         "                   count(if(correct=1,1,null))/count(*)*totalScore*\n" +
         "                   substring_index(substring_index(substr(punishRule,2,LENGTH(punishRule)-2),',',\n" +
         "                       if((@pre=problemId and @preUser=userId),(if (@resultId=temp.resultId,@s,@s:=@s+1)),@s:=1)),',',-1) as score,\n" +
         "                   @pre:=problemId,@preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
         "            from(select resultId,punishRule,submitTime,p.problemId,p.contestId,userId,totalScore,allowPartial\n" +
         "            from result join problem p on result.problemId = p.problemId order by userId,contestId,problemId,submitTime)\n" +
         "                temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,problemId,submitTime,temp.resultId order by userId,problemId,submitTime)\n" +
         "                score on score.resultId=r.resultId\n" +
         "join contest c on c.id=score.contestId\n" +
         "where r.userId like '%${userId}%' and r.problemId like '%${problemId}%' and stateCode like '%${stateCode}%' and c.id like '%${contestId}%' and c.classId like '%${classId}%'\n" +
         "            order by submitTime desc  " +
         "limit #{itemsPerPage} offset #{offset}")
    List<Result> getResult(@Param("userId") String userId,@Param("problemId") String problemId,@Param("stateCode") String stateCode,@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage,@Param("contestId")String contestId,@Param("classId")String classId);

    @Select("select count(*) from (select distinct result.resultId from result join User on User.id=result.userId " +
            "join problem on problem.problemId=result.problemId join checkpoint cp on cp.resultId=result.resultId" +
            " join contest c on problem.contestId=c.id "+
            " where result.userId like '%${userId}%' and result.problemId like '%${problemId}%' and cp.code like '%${stateCode}%' and c.id like '%${contestId}%' and c.classId like '%${classId}%' )temp")
    int getResultNum(@Param("userId") String userId,@Param("problemId") String problemId,@Param("stateCode") String stateCode,@Param("contestId")String contestId,@Param("classId")String classId);

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
    @Select("select u1.id as userId,name as userName,correctNum,answerNum,correctNum/answerNum as correctRate,@pr:=correctNum,rank()over(order by correctNum desc)as ranking\n" +
            "from User u1 join\n" +
            "(select u.id,sum(correct)as correctNum,sum(answerNum) as answerNum from User u join (select id,name,count(if(stateCode='AC',1,null))as correct,count(*) as answerNum\n" +
            "from User u join (select temp.resultId,userId,submitTime,allowPartial,\n" +
            "                               @preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "                        from(select resultId,punishRule,submitTime,userId,totalScore,allowPartial\n" +
            "                        from result join problem p on result.problemId = p.problemId order by userId,submitTime)\n" +
            "                            temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,submitTime,temp.resultId order by userId,submitTime)\n" +
            "                            score on score.userId=u.id group by id)up on up.id=u.id group by id)u2 on u1.id=u2.id,(select @r:=0,@pr:=0)q limit #{itemsPerPage} offset #{offset};")
    List<Rank>getRank(@Param("offset")int offset, @Param("itemsPerPage") int itemsPerPage);

    @Select("select count(*)\n" +
            "from User u1 join\n" +
            "(select u.id from User u join" +
            " (select id,name,count(if(stateCode='AC',1,null))as correct,count(*) as answerNum\n" +
            "from User u join (select temp.resultId,userId,submitTime,allowPartial,\n" +
            "                               @preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "                        from(select resultId,punishRule,submitTime,userId,totalScore,allowPartial\n" +
            "                        from result join problem p on result.problemId = p.problemId order by userId,submitTime)\n" +
            "                            temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,submitTime,temp.resultId order by userId,submitTime)\n" +
            "                            score on score.userId=u.id group by id)up on up.id=u.id group by id)u2 on u1.id=u2.id,(select @r:=0,@pr:=0)q ")
    int getRankNum();
    @Select("select u1.id as userId,name as userName,correctNum,answerNum,correctNum/answerNum as correctRate,rank()over(order by correctNum desc)as ranking,@pr:=correctNum from User u1 join (select u.id,sum(correct)as correctNum,sum(answerNum) as answerNum from User u join (select id,name,count(if(stateCode='AC',1,null))as correct,count(*) as answerNum  from User u join (select temp.resultId,userId,submitTime,allowPartial,\n" +
            "                   @preUser:=userId,@resultId:=temp.resultId,max(checkpoint.code) as stateCode\n" +
            "            from(select resultId,punishRule,submitTime,userId,totalScore,allowPartial\n" +
            "            from result join problem p on result.problemId = p.problemId order by userId,submitTime)\n" +
            "                temp join checkpoint on temp.resultId=checkpoint.resultId,(select @s:=0,@pre:=null,@preUser:=null,@resultId:=null)q group by userId,submitTime,temp.resultId order by userId,submitTime)\n" +
            "                score on score.userId=u.id group by id)up on up.id=u.id group by id)u2 on u1.id=u2.id ,(select @r:=0,@pr:=null)q where u2.id=#{userId}")
    List<Rank>getUserRank(@Param("userId")String userId);

    @Select("select u.id as userId,u.name as userName,shownId,if((max(a)-c.startTime)>0,(max(a)-c.startTime),0) as time,max(b) AS score,totalScore from User u join (\n" +
            "    select shownId,r.userId,contestId, r.submitTime,score.totalScore, (IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score,\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@maxScore:=0),\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@minTime:=0),\n" +
            "           @pre1:=r.problemId,@preUser1:=r.userId,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@minTime:=r.submitTime,@minTime)as a,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@maxScore:=score,@maxScore)as b\n" +
            "    from result r\n" +
            "             join (select temp.resultId,\n" +
            "                          userId,\n" +
            "                          shownId,\n" +
            "                          contestId,\n" +
            "                          problemId,\n" +
            "                          submitTime,\n" +
            "                          allowPartial,\n" +
            "                          totalScore,\n" +
            "                          (sum(if(correct = 1, 1, 0)) / count(*)) * totalScore *\n" +
            "                          substring_index(substring_index(substr(punishRule, 2, LENGTH(punishRule) - 2), ',',\n" +
            "                                                          if((@pre = problemId and @preUser = userId),\n" +
            "                                                             (if(@resultId = temp.resultId, @s, @s := @s + 1)),\n" +
            "                                                             @s := 1)), ',', -1) as score,\n" +
            "                          @pre := problemId,\n" +
            "                          @preUser := userId,\n" +
            "                          @resultId := temp.resultId,\n" +
            "                          max(checkpoint.code) as stateCode\n" +
            "                   from (select resultId,contestId, punishRule, submitTime,shownId, p.problemId, userId, totalScore, allowPartial\n" +
            "                         from result\n" +
            "                                  join problem p on result.problemId = p.problemId join contest c2 on p.contestId = c2.id where c2.endTime>result.submitTime\n" +
            "                         order by userId, problemId, submitTime) temp\n" +
            "                            join checkpoint on temp.resultId = checkpoint.resultId,\n" +
            "                        (select @s := 0, @pre := null, @preUser := null, @resultId := null) q\n" +
            "                   group by userId, problemId, submitTime, temp.resultId\n" +
            "                   order by userId, problemId, submitTime) score on score.resultId = r.resultId,\n" +
            "         (select @pre1 := null, @preUser1 := null,@maxScore:=0,@minTime:=0)m\n" +
            "    order by r.userId,r.problemId,submitTime\n" +
            ")s on s.userId=u.id join contest c on c.id=s.contestId  where contestId=#{contestId} and c.endTime>s.submitTime group by shownId,userId,totalScore order by userId,shownId;")
    List<UserResult> getContestResult(@Param("contestId")int contestId);

    @Select("select u.name as userName,shownId,if((max(a)-c.startTime)>0,(max(a)-c.startTime),0) as time,max(b) AS score,totalScore from User u join (\n" +
            "    select shownId,r.userId,contestId, r.submitTime,score.totalScore, (IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score,\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@maxScore:=0),\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@minTime:=0),\n" +
            "           @pre1:=r.problemId,@preUser1:=r.userId,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@minTime:=r.submitTime,@minTime)as a,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@maxScore:=score,@maxScore)as b\n" +
            "    from result r\n" +
            "             join (select temp.resultId,\n" +
            "                          userId,\n" +
            "                          shownId,\n" +
            "                          contestId,\n" +
            "                          problemId,\n" +
            "                          submitTime,\n" +
            "                          allowPartial,\n" +
            "                          totalScore,\n" +
            "                          count(if(correct = 1, 1, null)) / count(*) * totalScore *\n" +
            "                          substring_index(substring_index(substr(punishRule, 2, LENGTH(punishRule) - 2), ',',\n" +
            "                                                          if((@pre = problemId and @preUser = userId),\n" +
            "                                                             (if(@resultId = temp.resultId, @s, @s := @s + 1)),\n" +
            "                                                             @s := 1)), ',', -1) as score,\n" +
            "                          @pre := problemId,\n" +
            "                          @preUser := userId,\n" +
            "                          @resultId := temp.resultId,\n" +
            "                          max(checkpoint.code) as stateCode\n" +
            "                   from (select resultId,contestId, punishRule, submitTime,shownId, p.problemId, userId, totalScore, allowPartial\n" +
            "                         from result\n" +
            "                                  join problem p on result.problemId = p.problemId join contest c2 on p.contestId = c2.id where c2.endTime>result.submitTime\n" +
            "                         order by userId, problemId, submitTime,resultId) temp\n" +
            "                            join checkpoint on temp.resultId = checkpoint.resultId,\n" +
            "                        (select @s := 0, @pre := null, @preUser := null, @resultId := null) q\n" +
            "                   group by userId, problemId,temp.resultId\n" +
            "                   order by userId, problemId,submitTime,temp.resultId) score on score.resultId = r.resultId,\n" +
            "         (select @pre1 := null, @preUser1 := null,@maxScore:=0,@minTime:=0)m\n" +
            "    order by submitTime,userId,shownId,r.resultId\n" +
            ")s on s.userId=u.id join contest c on c.id=s.contestId  where contestId=#{contestId} and u.id=#{name} group by userId,shownId,totalScore order by userId,shownId;")
    List<UserResult> getContestResultByName(@Param("contestId")int contestId,@Param("name")String name);

    @Select("select User.id as userId,name as userName,sum(score) as score from User join\n" +
            "(select u.name as userName,shownId,if((max(a)-c.startTime)>0,(max(a)-c.startTime),0) as time,max(b) AS score,totalScore from User u join (\n" +
            "    select shownId,r.userId,contestId, r.submitTime,score.totalScore, (IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score,\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@maxScore:=0),\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@minTime:=0),\n" +
            "           @pre1:=r.problemId,@preUser1:=r.userId,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@minTime:=r.submitTime,@minTime)as a,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@maxScore:=score,@maxScore)as b\n" +
            "    from result r\n" +
            "             join (select temp.resultId,\n" +
            "                          userId,\n" +
            "                          shownId,\n" +
            "                          contestId,\n" +
            "                          problemId,\n" +
            "                          submitTime,\n" +
            "                          allowPartial,\n" +
            "                          totalScore,\n" +
            "                          count(if(correct = 1, 1, null)) / count(*) * totalScore *\n" +
            "                          substring_index(substring_index(substr(punishRule, 2, LENGTH(punishRule) - 2), ',',\n" +
            "                                                          if((@pre = problemId and @preUser = userId),\n" +
            "                                                             (if(@resultId = temp.resultId, @s, @s := @s + 1)),\n" +
            "                                                             @s := 1)), ',', -1) as score,\n" +
            "                          @pre := problemId,\n" +
            "                          @preUser := userId,\n" +
            "                          @resultId := temp.resultId,\n" +
            "                          max(checkpoint.code) as stateCode\n" +
            "                   from (select resultId,contestId, punishRule, submitTime,shownId, p.problemId, userId, totalScore, allowPartial\n" +
            "                         from result\n" +
            "                                  join problem p on result.problemId = p.problemId join contest c2 on p.contestId = c2.id where c2.endTime>result.submitTime\n" +
            "                         order by userId, problemId, submitTime) temp\n" +
            "                            join checkpoint on temp.resultId = checkpoint.resultId,\n" +
            "                        (select @s := 0, @pre := null, @preUser := null, @resultId := null) q\n" +
            "                   group by userId, problemId, submitTime, temp.resultId\n" +
            "                   order by userId, problemId, submitTime) score on score.resultId = r.resultId,\n" +
            "         (select @pre1 := null, @preUser1 := null,@maxScore:=0,@minTime:=0)m\n" +
            "    order by submitTime\n" +
            ")s on s.userId=u.id join contest c on c.id=s.contestId  where contestId=#{contestId} group by shownId,userId,totalScore)s1 on s1.userName=User.name group by User.id,userName order by score desc;")
    List<UserResult> getNameScore(@Param("contestId")int contestId);

    @Select("select u.id as userId,u.name as userName,shownId,if((max(a)-c.startTime)>0,(max(a)-c.startTime),0) as time,max(b) AS score,totalScore from User u join (\n" +
            "    select shownId,r.userId,contestId, r.submitTime,score.totalScore, (IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score,\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@maxScore:=0),\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@minTime:=0),\n" +
            "           @pre1:=r.problemId,@preUser1:=r.userId,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>=@maxScore,@minTime:=r.submitTime,@minTime)as a,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@maxScore:=score,@maxScore)as b\n" +
            "    from result r\n" +
            "             join (select temp.resultId,\n" +
            "                          userId,\n" +
            "                          shownId,\n" +
            "                          contestId,\n" +
            "                          problemId,\n" +
            "                          submitTime,\n" +
            "                          allowPartial,\n" +
            "                          totalScore,\n" +
            "                          count(if(correct = 1, 1, null)) / count(*) * totalScore *\n" +
            "                          substring_index(substring_index(substr(punishRule, 2, LENGTH(punishRule) - 2), ',',\n" +
            "                                                          if((@pre = problemId and @preUser = userId),\n" +
            "                                                             (if(@resultId = temp.resultId, @s, @s := @s + 1)),\n" +
            "                                                             @s := 1)), ',', -1) as score,\n" +
            "                          @pre := problemId,\n" +
            "                          @preUser := userId,\n" +
            "                          @resultId := temp.resultId,\n" +
            "                          max(checkpoint.code) as stateCode\n" +
            "                   from (select resultId,contestId, punishRule, submitTime,shownId, p.problemId, userId, totalScore, allowPartial\n" +
            "                         from result\n" +
            "                                  join problem p on result.problemId = p.problemId join contest c2 on p.contestId = c2.id where c2.endTime>result.submitTime\n" +
            "                         order by userId, problemId, submitTime) temp\n" +
            "                            join checkpoint on temp.resultId = checkpoint.resultId,\n" +
            "                        (select @s := 0, @pre := null, @preUser := null, @resultId := null) q\n" +
            "                   group by userId, problemId, submitTime, temp.resultId\n" +
            "                   order by userId, problemId, submitTime) score on score.resultId = r.resultId,\n" +
            "         (select @pre1 := null, @preUser1 := null,@maxScore:=0,@minTime:=0)m\n" +
            "    order by submitTime\n" +
            ")s on s.userId=u.id join contest c on c.id=s.contestId  where contestId=#{contestId} group by shownId,userId,totalScore order by userId,shownId;")
    List<UserResult> getLatestContestResult(@Param("contestId")int contestId);


    @Select("select name as userName,sum(score) as score from User join\n" +
            "(select u.name as userName,shownId,if((max(a)-c.startTime)>0,(max(a)-c.startTime),0) as time,max(b) AS score,totalScore from User u join (\n" +
            "    select shownId,r.userId,contestId, r.submitTime,score.totalScore, (IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)) as score,\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@maxScore:=0),\n" +
            "            if((@pre1 =r.problemId and @preUser1 =r.userId),@maxScore,@minTime:=0),\n" +
            "           @pre1:=r.problemId,@preUser1:=r.userId,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@minTime:=r.submitTime,@minTime)as a,\n" +
            "           If(IF(allowPartial = 0, if(stateCode = 'AC', score, 0), score)>@maxScore,@maxScore:=score,@maxScore)as b\n" +
            "    from result r\n" +
            "             join (select temp.resultId,\n" +
            "                          userId,\n" +
            "                          shownId,\n" +
            "                          contestId,\n" +
            "                          problemId,\n" +
            "                          submitTime,\n" +
            "                          allowPartial,\n" +
            "                          totalScore,\n" +
            "                          count(if(correct = 1, 1, null)) / count(*) * totalScore *\n" +
            "                          substring_index(substring_index(substr(punishRule, 2, LENGTH(punishRule) - 2), ',',\n" +
            "                                                          if((@pre = problemId and @preUser = userId),\n" +
            "                                                             (if(@resultId = temp.resultId, @s, @s := @s + 1)),\n" +
            "                                                             @s := 1)), ',', -1) as score,\n" +
            "                          @pre := problemId,\n" +
            "                          @preUser := userId,\n" +
            "                          @resultId := temp.resultId,\n" +
            "                          max(checkpoint.code) as stateCode\n" +
            "                   from (select resultId,contestId, punishRule, submitTime,shownId, p.problemId, userId, totalScore, allowPartial\n" +
            "                         from result\n" +
            "                                  join problem p on result.problemId = p.problemId join contest c2 on p.contestId = c2.id where c2.endTime>result.submitTime\n" +
            "                         order by userId, problemId, submitTime) temp\n" +
            "                            join checkpoint on temp.resultId = checkpoint.resultId,\n" +
            "                        (select @s := 0, @pre := null, @preUser := null, @resultId := null) q\n" +
            "                   group by userId, problemId, submitTime, temp.resultId\n" +
            "                   order by userId, problemId, submitTime) score on score.resultId = r.resultId,\n" +
            "         (select @pre1 := null, @preUser1 := null,@maxScore:=0,@minTime:=0)m\n" +
            "    order by submitTime,userId,shownId\n" +
            ")s on s.userId=u.id join contest c on c.id=s.contestId  where contestId=#{contestId} group by shownId,userId,totalScore)s1 on s1.userName=User.name where User.id=#{name} group by User.id,userName order by score desc;")
    List<UserResult> getNameScoreById(@Param("contestId")int contestId,@Param("name") String name);

    @Select("select code from result join User u on result.userId = u.id " +
            "join problem p on p.problemId = result.problemId " +
            "join contest c on c.id = p.contestId " +
            "where submitTime=#{time}+c.startTime and contestId=#{contestId} and u.name=#{userName} and shownId=#{shownId};")
    String getCode(@Param("contestId")int contestId,@Param("userName")String userName,@Param("shownId")String shownId,@Param("time")long time);
}
