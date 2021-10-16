package com.ooad.oj_backend;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Response {
    private int code;
    private String msg;
    private Object content;
    public Response(){

    }
    public Response(int code,String msg,Object content){
        this.code=code;
        this.msg=msg;
        this.content=content;
    }
}
