package com.ooad.oj_backend;

public class Response {
    private int code;
    private String msg;
    private Object content;
    public Response(int code,String msg,Object content){
        this.code=code;
        this.msg=msg;
        this.content=content;
    }
}
