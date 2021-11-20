package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Data
@Getter
@Setter
public class AddResult implements Serializable {

    private String UserId;
    private int status;
    private String detailed;

    public AddResult(){
        super();
    }
    public AddResult(String UserId,int status){
        super();
        this.UserId=UserId;
        this.status=status;
    }
}
