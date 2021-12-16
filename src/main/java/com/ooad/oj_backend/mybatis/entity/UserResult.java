package com.ooad.oj_backend.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class UserResult implements Serializable {
    private String userName;
    private String userId;
    private String shownId;
    private long time;
    private int score;
    private int totalScore;
}
