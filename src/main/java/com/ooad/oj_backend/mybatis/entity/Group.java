package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Data
@Getter
@Setter
public class Group implements Serializable {

    private int id;
    private String name;
    public Group(){
        super();
    }
    public Group(int id,String name){
        super();
        this.id=id;
        this.name=name;
    }
}
