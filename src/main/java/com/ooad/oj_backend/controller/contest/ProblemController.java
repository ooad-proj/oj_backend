package com.ooad.oj_backend.controller.contest;

import com.ooad.oj_backend.mybatis.entity.Problem;
import com.ooad.oj_backend.mybatis.entity.Samples;
import com.ooad.oj_backend.mybatis.entity.ScoreRule;
import com.ooad.oj_backend.mybatis.entity.SubmitTemplate;
import com.ooad.oj_backend.service.contest.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/problem/")
public class ProblemController {
    @Autowired
    private ProblemService problemService;
    @RequestMapping(value = "answering",method = RequestMethod.GET)
    public ResponseEntity<?> getProblem(String search,int page,int itemsPerPage) {
        return problemService.getProblem(search,page,itemsPerPage);
    }
    @RequestMapping(value = "admin",method = RequestMethod.GET)
    public ResponseEntity<?> getProblemByPrivilege(String search,int page,int itemsPerPage) {
        return problemService.getProblemByPrivilege(search,page,itemsPerPage);
    }
    @RequestMapping(value = "answering/{problemId}",method = RequestMethod.GET)
    public ResponseEntity<?> getDetailedProblem(@PathVariable int problemId) {
        return problemService.getDetailedProblem(problemId);
    }
    @PostMapping(value = "contest/{contestId}")
    public ResponseEntity<?> addProblem(@PathVariable int contestId, int shownId, String title, ScoreRule scoreRule,
                                        Samples[] samples, String description, String inputFormat, String outputFormat,
                                        SubmitTemplate[] submitTemplate,String tips,String timeLimit,String spaceLimit,
                                        String allowedLanguage,String testCaseId) {
        return problemService.addProblem(contestId,shownId,title,scoreRule,samples,description,inputFormat,
                outputFormat,submitTemplate,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId);
    }
    @RequestMapping(value = "{problemId}",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProblem(@PathVariable int problemId) {
        return problemService.deleteProblem(problemId);
    }
    @RequestMapping(value = "{problemId}",method = RequestMethod.PUT)
    public ResponseEntity<?> updateProblem(@PathVariable int problemId, int shownId, String title, ScoreRule scoreRule,
                                           Samples[] samples, String description, String inputFormat, String outputFormat,
                                           SubmitTemplate[] submitTemplate,String tips,String timeLimit,String spaceLimit,
                                           String allowedLanguage,String testCaseId) {
        return problemService.updateProblem(problemId,shownId,title,scoreRule,samples,description,inputFormat,
                outputFormat,submitTemplate,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId);
    }
    @PostMapping(value = "testCase/{contestId}")
    public ResponseEntity<?> addTestCase(@PathVariable int contestId, MultipartFile file) {
        return problemService.addTestCase(contestId,file);
    }
    @PostMapping(value = "standardAnswer/{problemId}")
    public ResponseEntity<?> addAnswer(@PathVariable int problemId,String answer,String language) {
        return problemService.addAnswer(problemId,language,answer);
    }
    @RequestMapping(value = "standardAnswer/{answerId}",method=RequestMethod.PUT)
    public ResponseEntity<?> updateAnswer(@PathVariable int answerId,String answer,String language) {
        return problemService.updateAnswer(answerId,language,answer);
    }
    @RequestMapping(value = "standardAnswer/{problemId}",method=RequestMethod.GET)
    public ResponseEntity<?> getAnswer(@PathVariable int problemId) {
        return problemService.getAnswer(problemId);
    }
    @RequestMapping(value = "standardAnswer/{answerId}",method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAnswer(@PathVariable int answerId) {
        return problemService.deleteAnswer(answerId);
    }
}
