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
    int groupId;
    int contestId;
    boolean allowPartial=false;
    String code;
    String stateCode;
    public Result(String resultId,long submitTime,String userId,int problemId, String code){
        this.resultId=resultId;
        this.submitTime=submitTime;
        this.userId=userId;
        this.problemId=problemId;
        this.code = code;
    }
}
