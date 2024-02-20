package com.yaser.news.controller.globalHandler;

import com.yaser.news.constant.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult<T> implements Serializable {

    private T data = null;
    private int code;
    @NonNull
    private String message;

    public HttpResult<T> sendSuccess(T data) {
        return this.send(ResultCode.SUCCESS, data);
    }

    public HttpResult<T> send(ResultCode resultCode, T data) {
        this.data = data;
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        return this;
    }

    public HttpResult(int code, @NonNull String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResult(ResultCode resultCode, T data) {
        this.send(resultCode, data);
    }

}
