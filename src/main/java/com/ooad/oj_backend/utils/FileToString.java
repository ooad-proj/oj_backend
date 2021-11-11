package com.ooad.oj_backend.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class FileToString {

    public static String fileToString(String path) {
        BufferedInputStream in = FileUtil.getInputStream(new File(path));
        byte[] bs = IoUtil.readBytes(in);
        return Base64.getEncoder().encodeToString(bs);
    }

    public static void stringToFile(String path, String str) {
        BufferedOutputStream os = FileUtil.getOutputStream(path);
        try {
            os.write(Base64.getDecoder().decode(str));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
