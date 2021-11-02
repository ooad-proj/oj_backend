package com.ooad.oj_backend.mapper.contest;

import com.ooad.oj_backend.mybatis.entity.*;
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

    @Select("        SELECT\n" +
            "        count(*)\n" +
            "        FROM problem p join contest on contest.id=p.contestId " +
            "join class on class.id=contest.classId " +
            "join auth a on a.classId=class.id where ${check},userId=#{userId} and privilege=1 or contest.id=0")
    int checkProblemPrivilege(@Param("check")String check,@Param("userId")String userId);

    @Select("        SELECT\n" +
            "        count(*)\n" +
            "        FROM problem p join contest on contest.id=p.contestId " +
            "join class on class.id=contest.classId " +
            "join auth a on a.classId=class.id join answer a2 on p.problemId = a2.problemId where ${check},userId=#{userId} and privilege=1 or contest.id=0")
    int checkAnswerPrivilege(@Param("check")String check,@Param("userId")String userId);

    @Select("        SELECT\n" +
            "        problemId,shownId,title\n" +
            "        FROM problem where contestId =#{contestId} ")
    List<Problem> getContestProblem(int contestId);
    @Select("select shownId,title,description,inputFormat,outputFormat,input,output,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId,totalScore,punishRule,allowPartial from problem " +
            "join samples s on problem.problemId = s.problemId join scoreRule sR on s.problemId = sR.problemId where problem.problemId=#{problemId};")
    Problem getDetailedProblem(int problemId);

    @Select("select problem.creatorId,User.name,c2.id as groupId,c2.name as groupName from problem " +
            "join User on problem.creatorId = User.id join contest c on c.id = problem.contestId " +
            "join class c2 on c2.id = c.classId where problem.problemId=#{problemId}")
    CreatorAndGroup getCreatorAndGroup(int problemId);
    @Insert("insert into problem values (null,#{shownId},#{title},#{contestId},#{description},#{inputFormat},#{outputFormat}" +
            ",#{tips},#{timeLimit},#{spaceLimit},#{testCaseId},#{allowedLanguage},#{creatorId});")
    @Options(useGeneratedKeys = true, keyProperty = "problemId", keyColumn="problemId")
    int addProblem(@Param("contestId") int contestId,@Param("shownId") int shownId, @Param("title")String title,
                    @Param("description")String description,@Param("inputFormat") String inputFormat,@Param("outputFormat") String outputFormat,
                    @Param("tips")String tips,@Param("timeLimit")String timeLimit,@Param("spaceLimit")String spaceLimit,
                    @Param("allowedLanguage")String allowedLanguage,@Param("testCaseId")String testCaseId);
    @Insert("inset into samples values(#{problemId},#{input},#{output});" )
    void addScoreRule(@Param("problemId")int problemId, @Param("totalScore")int totalScore,
                      @Param("allowPartial")boolean allowPartial,@Param("punishRule")String punishRule);

    @Insert("inset into samples values(#{problemId},#{input},#{output});" )
    void addSample(@Param("problemId")int problemId,@Param("input")String input, @Param("output")String output);

    @Update("update problem set shownId=#{shownId},title=#{title},description=#{description},inputFormat=#{inputFormat}," +
            "outputFormat=#{outputFormat},tips=#{tips},timeLimit=#{timeLimit},spaceLimit=#{spaceLimit}," +
            "testCaseId=#{testCaseId},allowedLanguage=#{allowedLanguage},creatorId=#{creatorId}\n" +
            "where problemId=#{problemId};" +
            "update scoreRule set totalScore=#{totalScore},allowPartial=#{allowPartial},punishRule=#{punishRule} " +
            "where problemId=#{problemId};")
    void updateProblem(@Param("problemId") int problemId,@Param("shownId") int shownId, @Param("title")String title,
                   @Param("totalScore")int totalScore, @Param("allowPartial")boolean allowPartial,@Param("punishRule")String punishRule
            ,@Param("description")String description,@Param("inputFormat") String inputFormat,@Param("outputFormat") String outputFormat,
                   @Param("tips")String tips,@Param("timeLimit")String timeLimit,@Param("spaceLimit")String spaceLimit,
                   @Param("allowedLanguage")String allowedLanguage,@Param("testCaseId")String testCaseId);

    @Insert("inset into submitTemplate values(#{problemId},#{language},#{code})" )
    void addSubmitTemplate(@Param("problemId")int problemId,@Param("language")String language,@Param("code")String code);

    @Select("select contestId from problem where problemId=#{problemId}")
    int getContestId(int problemId);
    @Delete("       DELETE FROM samples WHERE problemId =#{problemId};" +
            " DELETE FROM scoreRule WHERE problemId =#{problemId};" +
            " DELETE FROM submitTemplate WHERE problemId =#{problemId};" +
            " DELETE FROM problem WHERE problemId =#{problemId};")
    void deleteProblem(int problemId);

    @Delete("       DELETE FROM samples WHERE problemId =#{problemId};")
    void deleteSamples(int problemId);

    @Delete("       DELETE FROM submitTemplate WHERE problemId =#{problemId};")
    void deleteSubmitTemplates(int problemId);

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
