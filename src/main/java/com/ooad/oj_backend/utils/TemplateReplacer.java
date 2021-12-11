package com.ooad.oj_backend.utils;

import com.ooad.oj_backend.rabbitmq.entity.Template;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateReplacer {
    static String patternJava = "^([\\s\\S]*)//--//--([\\s\\S]*)//--//--([\\s\\S]*)$";
    static String patternPython = "^([\\s\\S]*)##--##--([\\s\\S]*)##--##--([\\s\\S]*)$";


    public static String getMiddle(String template, String language) {
        Pattern pattern = Pattern.compile(language.equals("java") ? patternJava : patternPython, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(template);
        if (matcher.find()) {
            return matcher.group(2);

        }
        return "No valid template found.";
    }

}
