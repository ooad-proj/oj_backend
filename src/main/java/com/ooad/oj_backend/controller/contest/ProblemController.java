package com.ooad.oj_backend.controller.contest;

import com.ooad.oj_backend.service.contest.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> addProblem(@PathVariable int contestId) {
        return problemService.addProblem(contestId);
    }
    @RequestMapping(value = "{problemId}",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProblem(@PathVariable int problemId) {
        return problemService.deleteProblem(problemId);
    }
    @RequestMapping(value = "{problemId}",method = RequestMethod.PUT)
    public ResponseEntity<?> updateProblem(@PathVariable int problemId) {
        return problemService.updateProblem(problemId);
    }
    @PostMapping(value = "testCase")
    public ResponseEntity<?> addTestCase() {
        return problemService.addTestCase();
    }
    @PostMapping(value = "{problemId}/standardAnswer")
    public ResponseEntity<?> addAnswer(@PathVariable int problemId,String answer,String language) {
        return problemService.addAnswer(problemId,answer,language);
    }
    @RequestMapping(value = "standardAnswer/{problemId}",method=RequestMethod.PUT)
    public ResponseEntity<?> updateAnswer(@PathVariable int problemId,String answer,String language) {
        return problemService.updateAnswer(problemId,answer,language);
    }
    @RequestMapping(value = "standardAnswer/{problemId}",method=RequestMethod.GET)
    public ResponseEntity<?> getAnswer(@PathVariable int problemId) {
        return problemService.getAnswer(problemId);
    }
    @RequestMapping(value = "standardAnswer/{problemId}",method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAnswer(@PathVariable int problemId) {
        return problemService.deleteAnswer(problemId);
    }
}
