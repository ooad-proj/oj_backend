package com.ooad.oj_backend.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Rank implements Serializable {
    private String userId;
    private String userName;
    private int correctNum;
    private int answerNum;
    private double correctRate;
    private int i;
}
