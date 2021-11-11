package com.ooad.oj_backend.rabbitmq.entity;

import java.util.ArrayList;
import java.util.List;

public class JudgeDetail {
    public String language = "";
    public String code = "";
    public int timeLimit = 2000;
    public int memoryLimit = 128;
    public List<Template> languageTemplates = new ArrayList<>();

    public JudgeDetail(String language, String code, int timeLimit, int memoryLimit, List<Template> languageTemplates) {
        this.language = language;
        this.code = code;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.languageTemplates = languageTemplates;
    }
}
