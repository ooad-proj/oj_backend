package com.ooad.oj_backend.rabbitmq.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result implements Comparable<Result> {
    int id = 1;
    int total = 1;
    boolean correct = false;
    int timeCost = 0;
    int memoryCost = 0;
    String code = "UND";
    String name = "Undefined";
    String message = "default message.";
    Color color = Color.GRAY;

    public Result() {}

    public Result(int id, int total, boolean correct, int timeCost, int memoryCost, String code, String name, String message, Color color) {
        this.id = id;
        this.total = total;
        this.correct = correct;
        this.timeCost = timeCost;
        this.memoryCost = memoryCost;
        this.code = code;
        this.name = name;
        this.message = message;
        this.color = color;
    }

    public static Result AC(int id, int total, String message, int timeCost, int memoryCost) {
        return new Result(id, total, true, timeCost, memoryCost, "AC", "Accepted", message, Color.GREEN);
    }

    public static Result WA(int id, int total, String message, int timeCost, int memoryCost) {
        return new Result(id, total, false, timeCost, memoryCost, "WA", "Wrong Answer", message, Color.RED);
    }

    public static Result CE(int id, int total, String message) {
        return new Result(id, total, false, 0, 0, "CE", "Compile Error", message, Color.PURPLE);
    }

    public static Result RE(int id, int total, String message) {
        return new Result(id, total, false, 0, 0, "RE", "Runtime Error", message, Color.ORANGE);
    }

    public static Result TLE(int id, int total, String message) {
        return new Result(id, total, false, 0, 0, "TLE", "Time Limit Exceeded", message, Color.ORANGE);
    }

    public static Result MLE(int id, int total, String message) {
        return new Result(id, total, false, 0, 0, "MLE", "Memory Limit Exceeded", message, Color.ORANGE);
    }

    public static Result NS(int id, int total, String message) {
        return new Result(id, total, false, 0, 0, "NS", "Not Supported", message, Color.GRAY);
    }

    @Override
    public int compareTo(Result o) {
        return this.id - o.id;
    }
}
