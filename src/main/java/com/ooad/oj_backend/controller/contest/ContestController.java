package com.ooad.oj_backend.controller.contest;

import com.ooad.oj_backend.service.contest.ContestService;
import com.ooad.oj_backend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/")
public class ContestController {
    @Autowired
    private ContestService contestService;


    @RequestMapping(value = "/contest/{contestId}/answering", method = RequestMethod.GET)
    public ResponseEntity<?> getContestInformation( @PathVariable int contestId) {
        return contestService.getContestInformation(contestId);
    }


    @RequestMapping(value = "/contest/admin", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getManagingContests( int page, int itemsPerPage,String search) {
        return contestService.getManagingContests(page,itemsPerPage,search);
    }

    @RequestMapping(value = "/contest/group/{groupId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addContest( @PathVariable int groupId , String title, String description, long startTime, long endTime,boolean access) {
        return contestService.addContest(groupId,title,description,startTime,endTime,access);
    }

    @RequestMapping(value = "/contest/{contestId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> modifyContest ( @PathVariable int contestId , String title, String description, long startTime, long endTime,boolean access ) {
        return contestService.modifyContest(contestId,title,description,startTime,endTime,access);
    }

    @RequestMapping(value = "/contest/{contestId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteContest ( @PathVariable int contestId ) {
        return contestService.deleteContest(contestId);
    }

    @RequestMapping(value = "/contest/ddl", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCloseContest (  int amount ) {
        return contestService.getCloseContest(amount);
    }

    @RequestMapping(value = "/contest/{contestId}/rank", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getContestRank (@PathVariable int contestId) {
        return contestService.getContestRank(contestId);
    }

    @RequestMapping(value = "/contest/{contestId}/export/accepted", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAcceptedCode (@PathVariable int contestId) {
        return contestService.getAcceptedCode(contestId);
    }
    @RequestMapping(value = "/contest/{contestId}/export/score", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getStudentScore (@PathVariable int contestId) throws IOException {
        return contestService.getStudentScore(contestId);
    }
}
