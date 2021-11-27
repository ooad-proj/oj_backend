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

    @Select("select * from checkpoint where resultId = #{submitId}"
    )
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
}
