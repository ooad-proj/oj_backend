package com.ooad.oj_backend.mybatis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Answer implements Serializable{
    private String language;
    private String code;
    private boolean isStandard;

    @JsonProperty(value = "isStandard")
    public boolean isStandard() {
        return isStandard;
    }



}
