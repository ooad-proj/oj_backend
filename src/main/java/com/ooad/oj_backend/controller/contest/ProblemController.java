package com.ooad.oj_backend.controller.contest;

import com.ooad.oj_backend.mybatis.entity.*;
import com.ooad.oj_backend.service.contest.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
   /* public ResponseEntity<?> addProblem(@PathVariable int contestId, int shownId, String title, ScoreRule scoreRule,
                                        @RequestParam(value = "samples[]") Samples[] samples, String description, String inputFormat, String outputFormat,
                                        @RequestParam(value = "submitTemplate[]")SubmitTemplate[] submitTemplate,String tips,String timeLimit,String spaceLimit,
                                        String allowedLanguage,String testCaseId) {
        return problemService.addProblem(contestId,shownId,title,scoreRule,samples,description,inputFormat,
                outputFormat,submitTemplate,tips,timeLimit,spaceLimit,allowedLanguage,testCaseId);
    }*/
    public ResponseEntity<?> addProblem(@PathVariable int contestId,@RequestBody Problem problem) {
        return problemService.addProblem(contestId,problem);
    }
    @RequestMapping(value = "{problemId}",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProblem(@PathVariable int problemId) {
        return problemService.deleteProblem(problemId);
    }
    @RequestMapping(value = "{problemId}",method = RequestMethod.PUT)
    public ResponseEntity<?> updateProblem(@PathVariable int problemId,@RequestBody Problem problem) {
        return problemService.updateProblem(problemId,problem);
    }
    @PostMapping(value = "testCase/{contestId}")
    public ResponseEntity<?> addTestCase(@PathVariable int contestId, MultipartFile file) {
        return problemService.addTestCase(contestId,file);
    }
    @GetMapping(value = "testCase/{testCaseId}/download")
    public ResponseEntity<?> downloadTestCase(@PathVariable("testCaseId")String testCaseId) {
        return problemService.downloadTestCase(testCaseId);
    }
    @RequestMapping(value = "standardAnswer/{problemId}",method=RequestMethod.PUT)
    public ResponseEntity<?> updateAnswer(@PathVariable int problemId,@RequestBody AnswerList answer) {
        return problemService.updateAnswer(problemId,answer);
    }
    @RequestMapping(value = "standardAnswer/{problemId}",method=RequestMethod.GET)
    public ResponseEntity<?> getAnswer(@PathVariable int problemId) {
        return problemService.getAnswer(problemId);
    }
}
