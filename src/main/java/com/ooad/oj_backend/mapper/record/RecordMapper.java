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
            "         checkpoint (id,total,correct,timeCost,memoryCost,code,name,message,color)\n" +
            "       VALUE\n" +
            "       (#{id},#{total},#{correct},#{timeCost},#{memoryCost},#{code},#{name},#{message},#{color})")
    void addCheckpoint(com.ooad.oj_backend.rabbitmq.entity.Result result);

}
