package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UserView implements Serializable {

    private String id;
    private String name;
    private String mail;
    public UserView(){
        super();
    }
    public UserView(String id,String name,String mail){
        super();
        this.id=id;
        this.name=name;
        this.mail=mail;
    }
}
