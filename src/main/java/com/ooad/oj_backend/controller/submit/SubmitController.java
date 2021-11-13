package com.ooad.oj_backend.controller.submit;

import com.ooad.oj_backend.Response;
import com.ooad.oj_backend.service.submit.SubmitService;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/")
public class SubmitController {
    @Autowired
    private SubmitService submitService;
    @RequestMapping(value = "submit/{problemId}",method = RequestMethod.POST)
    public ResponseEntity<?> submitCode(@PathVariable int problemId) {
        return submitService.submitCode(problemId);
    }
    @RequestMapping(value = "submit/standard/{problemId}",method = RequestMethod.GET)
    public ResponseEntity<?> standardAnsTest(@PathVariable int problemId,String testcase) {
        return submitService.standardAnsTest(problemId, testcase);
    }

    @RequestMapping(value = "auth/submit/haveAnswer/{problemId}",method = RequestMethod.GET)
    public ResponseEntity<?> AskIfhaveAnswer(@PathVariable int problemId){
        return submitService.AskIfhaveAnswer(problemId);
    }
}
