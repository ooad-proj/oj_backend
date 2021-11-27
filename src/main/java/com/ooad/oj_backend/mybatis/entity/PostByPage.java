package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class PostByPage implements Serializable {
    int postId ;
    String title;
    String content;
    String userId;
    String userName;
    Long modifyTime;
}
