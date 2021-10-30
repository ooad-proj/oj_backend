package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class Problem implements Serializable {
    private int problemId;
    private String shownId;
    private String title;
    private int contestId;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String tips;
    private long timeLimit;
    private long spaceLimit;
    private String testCaseId;
    private String allowedLanguage;
    //private ScoreRule scoreRule;
    //private Samples samples;
}