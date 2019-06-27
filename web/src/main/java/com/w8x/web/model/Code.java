package com.w8x.web.model;

import lombok.Data;

@Data
public class Code<T> {
    private int code;
    private T data;
    private String msg;
    private Code(int code,T data,String msg){
        this.code =code;
        this.data = data;
        this.msg = msg;
    }
    public static <T> Code<T> createCode(int code, T data, String msg){
        return new Code(code,data,msg);
    }
    //数据结构 + 算法 + 设计模式
}
