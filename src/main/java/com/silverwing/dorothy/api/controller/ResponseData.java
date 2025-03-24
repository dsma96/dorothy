package com.silverwing.dorothy.api.controller;

import lombok.Getter;

@Getter
public class ResponseData <T>{
    String msg;
    int code;
    T payload;

    public ResponseData(String msg) {
        this.msg = msg;
        this.code = 500;
    }

    public ResponseData(String msg, int code, T data) {
        this.msg = msg;
        this.code = code;
        this.payload = data;
    }
}
