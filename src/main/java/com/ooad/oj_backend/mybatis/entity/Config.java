package com.ooad.oj_backend.mybatis.entity;

import java.io.File;

public class Config {
    public static String path=System.getProperty("user.home") + File.separator+ "oj";
    public static String testCaseStore=System.getProperty("user.home") + File.separator+ "testCaseStore";
}
