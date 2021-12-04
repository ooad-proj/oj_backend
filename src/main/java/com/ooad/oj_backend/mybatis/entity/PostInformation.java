package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class PostInformation implements Serializable {
    String userName;
    String userId;
    Long modifyTime;
    String title;
    String content;
    Boolean goPublic;
    int groupId;
}
