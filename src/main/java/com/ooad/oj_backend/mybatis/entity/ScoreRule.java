package com.ooad.oj_backend.mybatis.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ScoreRule implements Serializable {
    private int totalScore;
    private String punishRule;
    private boolean allowPartial;
}
