package com.ooad.oj_backend.rabbitmq.entity;

import java.io.Serializable;

public class Template implements Serializable {
    String language = "";
    String code = "";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
