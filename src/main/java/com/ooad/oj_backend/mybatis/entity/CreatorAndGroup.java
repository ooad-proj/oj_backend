package com.ooad.oj_backend.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreatorAndGroup implements Serializable {
    private int classId;
    private String className;
    private String groupId;
    private String groupName;
}
