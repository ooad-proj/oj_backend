package com.ooad.oj_backend.mybatis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class AnswerList implements Serializable {
    Answer[]answer;
    boolean isPublish;
    @JsonProperty(value = "isPublish")
    public boolean isPublish() {
        return isPublish;
    }
}
