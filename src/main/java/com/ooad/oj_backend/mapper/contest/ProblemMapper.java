package com.ooad.oj_backend.mapper.contest;

import com.ooad.oj_backend.mybatis.entity.Problem;
import com.ooad.oj_backend.mybatis.entity.ProblemView;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface ProblemMapper {
    @Select("        SELECT\n" +
            "        p.problemId,p.title,a.classId as groupId,class.name as groupName\n" +
            "        FROM problem p join contest on contest.id=p.contestId " +
            "join class on class.id=contest.classId " +
            "join auth a on a.classId=class.id where p.title like '%${search}%' ${userId} limit #{itemsPerPage} offset #{offset}")
    List <ProblemView> getProblem(@Param("search") String search,@Param("userId") String userId,@Param("offset")int offset,@Param("itemsPerPage") int itemsPerPage);
    @Select("        SELECT\n" +
            "        count(*)\n" +
            "        FROM problem p join contest on contest.id=p.contestId " +
            "join class on class.id=contest.classId " +
            "join auth a on a.classId=class.id where p.title like '%${search}%' ${userId}")
    int getProblemNumber(@Param("search") String search,@Param("userId") String userId);

    @Insert("       INSERT INTO\n" +
            "         class\n" +
            "       (name)\n" +
            "       VALUES\n" +
            "       (#{name})")
    void insert();
}
