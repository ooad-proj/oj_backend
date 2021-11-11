package com.ooad.oj_backend.rabbitmq.entity;

import com.google.gson.Gson;
import com.ooad.oj_backend.utils.FileToString;

public class SendPacket {
    //type == 0: from file
    // type == 1: from input
    int type;
    String submitId;
    String file;
    String input;
    JudgeDetail judgeDetail;

    public JudgeDetail getJudgeDetail() {
        return judgeDetail;
    }

    public int getType() {
        return type;
    }

    public String getInput() {
        return input;
    }

    public SendPacket(String submitId, int type, String file, String input, JudgeDetail judgeDetail) {
        this.submitId = submitId;
        this.file = file;
        this.judgeDetail = judgeDetail;
        this.input = input;
        this.type = type;
    }

    public SendPacket(String submitId, String filePath, JudgeDetail judgeDetail) {
        this.submitId = submitId;
        this.file = FileToString.fileToString(filePath);
        this.judgeDetail = judgeDetail;
        this.type = 0;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SendPacket fromString(String str) {
        Gson gson = new Gson();
        return gson.fromJson(str, SendPacket.class);
    }
}
