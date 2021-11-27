package com.ooad.oj_backend.mybatis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    String resultId;
    long submitTime;
    String userId;
    int problemId;
    double score;
    boolean allowPartial=false;
    public Result(String resultId,long submitTime,String userId,int problemId){
        this.resultId=resultId;
        this.submitTime=submitTime;
        this.userId=userId;
        this.problemId=problemId;
    };
}
