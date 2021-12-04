package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class CommentByPage implements Serializable {
    int commentId ;
    int floorId;
    String userId;
    String userName;
    String comment;
    Long modifyTime;
    Boolean deleteable;
}
