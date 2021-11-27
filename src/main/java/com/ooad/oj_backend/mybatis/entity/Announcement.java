package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class Announcement implements Serializable {
    int postId;
    String title;
    String preview;
    String userId;
    String userName;
    long modifyTime;
}
