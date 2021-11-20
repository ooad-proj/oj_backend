package com.ooad.oj_backend.controller.record;

import com.ooad.oj_backend.mybatis.entity.Problem;
import com.ooad.oj_backend.service.contest.ProblemService;
import com.ooad.oj_backend.service.record.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/record/")
public class RecordController {
    @Autowired
    private RecordService recordService;

    @RequestMapping(value = "{recordId}",method = RequestMethod.GET)
    public ResponseEntity<?> getProblem(@PathVariable String recordId) {
        return recordService.getRecord(recordId);
    }
    @RequestMapping(value = "standard/{recordId}",method = RequestMethod.GET)
    public ResponseEntity<?> getStandardTest(@PathVariable String recordId) {
        return recordService.getStandardTestRecord(recordId);
    }
}
