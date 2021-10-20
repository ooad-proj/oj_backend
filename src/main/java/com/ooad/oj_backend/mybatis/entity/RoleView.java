package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;

@Data
@Getter
@Setter
public class RoleView implements Serializable {

    private int groupId;
    private String groupName;
    private String role;
    public RoleView(){
        super();
    }
    public RoleView(int groupId,String groupName,String role){
        super();
        this.groupId=groupId;
        this.groupName=groupName;
        this.role=role;
    }
}
