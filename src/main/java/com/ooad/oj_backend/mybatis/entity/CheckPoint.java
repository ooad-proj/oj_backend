package com.ooad.oj_backend.mybatis.entity;

import com.ooad.oj_backend.rabbitmq.entity.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckPoint {
    int checkpointId;
    int resultId;
    int id;
    int total;
    int timeCost;
    int memoryCost;
    String code;
    String name;
    String message;
    String color;

    public CheckPoint(int checkpointId, int resultId, Result result) {
        this.checkpointId = checkpointId;
        this.resultId = resultId;
        this.id = result.getId();
        this.total = result.getTotal();
        this.timeCost = result.getTimeCost();
        this.memoryCost = result.getMemoryCost();
        this.code = result.getCode();
        this.name = result.getName();
        this.message = result.getMessage();
        this.color = result.getColor().toString();
    }
}
