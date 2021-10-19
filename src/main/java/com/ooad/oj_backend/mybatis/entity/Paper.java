package com.ooad.oj_backend.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class Paper<T> {
    int page;
    int itemsPerPage;
    int totalAmount;
    int totalPage;
    List<T> list;
}
