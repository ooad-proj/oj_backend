package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Data
@Getter
@Setter
public class Auth implements Serializable {

    private String UserId;
    private int classId;
    private int privilege;

    public Auth(){
        super();
    }
    public Auth(String UserId,int classId,int privilege){
        super();
       this.UserId=UserId;
       this.classId=classId;
       this.privilege=privilege;
    }
}
