package com.ooad.oj_backend.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProblemView implements Serializable {
    private int problemId;
    private String title;
    private int groupId;
    private String groupName;
}
