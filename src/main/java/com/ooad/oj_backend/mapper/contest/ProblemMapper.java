package com.ooad.oj_backend.mapper.contest;

import com.ooad.oj_backend.mybatis.entity.Answer;
import com.ooad.oj_backend.mybatis.entity.CreatorAndGroup;
import com.ooad.oj_backend.mybatis.entity.Problem;
import com.ooad.oj_backend.mybatis.entity.ProblemView;
import lombok.Setter;
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

    @Select("select shownId,title,description,inputFormat,outputFormat,input,output,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId,totalScore,punishRule,allowPartial from problem " +
            "join samples s on problem.problemId = s.problemId join scoreRule sR on s.problemId = sR.problemId where problem.problemId=#{problemId};")
    Problem getDetailedProblem(int problemId);

    @Select("select problem.creatorId,User.name,c2.id as groupId,c2.name as groupName from problem " +
            "join User on problem.creatorId = User.id join contest c on c.id = problem.contestId " +
            "join class c2 on c2.id = c.classId where problem.problemId=#{problemId}")
    CreatorAndGroup getCreatorAndGroup(int problemId);
    @Insert("       INSERT INTO\n" +
            "         answer(problemId,language,code)\n" +
            "       VALUES\n" +
            "       (#{problemId},#{language},#{code})")
    void addAnswer(@Param("problemId") int problemId,@Param("language") String language,@Param("code") String code);

    @Select("Select count(*) from problem where problemId=#{problemId}")
    int searchProblem(int problemId);

    @Select("Select * from answer where problemId=#{problemId}")
    List<Answer> getAnswerByProblem(int problemId);

    @Select("Select * from answer where answerId=#{answerId}")
    Answer getAnswerById(int answerId);
    @Update("       UPDATE\n" +
            "        answer SET \n" +
            "       code=#{code}," +
            " language=#{language} where answerId=#{answerId}")
    void updateAnswer(@Param("answerId") int answerId, @Param("language") String language,@Param("code")String code);

    @Delete("       DELETE FROM\n" +
            "            answer\n" +
            "       WHERE \n" +
            "       answerId =#{answerId}")
    void deleteAnswer(int answerId);
}
