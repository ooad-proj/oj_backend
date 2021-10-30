package com.ooad.oj_backend.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Answer implements Serializable {
    private int answerId;
    private String language;
    private String code;
}
