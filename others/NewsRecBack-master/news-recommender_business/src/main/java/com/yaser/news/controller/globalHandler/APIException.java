package com.yaser.news.controller.globalHandler;

import com.yaser.news.constant.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class APIException extends RuntimeException {
    private int code;
    private String message;

    public APIException(String message) {
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }

    public APIException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public APIException() {
        this.code = ResultCode.ERROR.getCode();
        this.message = ResultCode.ERROR.getMessage();
    }
}
