package com.ooad.oj_backend.rabbitmq.entity;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecvPacket {
    //type == 0: standard
    //type == 1: end
    int type;
    String submitId;
    Result result;

    public RecvPacket(int type, Result result) {
        this.type = type;
        this.result = result;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static RecvPacket fromString(String str) {
        Gson gson = new Gson();
        return gson.fromJson(str, RecvPacket.class);
    }

}
