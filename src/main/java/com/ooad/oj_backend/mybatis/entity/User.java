package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Data
@Getter
@Setter
public class User implements Serializable {

    private String id;
    private String name;
    private String password;
    private String mail;
    public User(){
        super();
    }
    public User(String id,String name,String password,String mail){
        super();
        this.id=id;
        this.name=name;
        this.password=password;
        this.mail=mail;
    }
}
