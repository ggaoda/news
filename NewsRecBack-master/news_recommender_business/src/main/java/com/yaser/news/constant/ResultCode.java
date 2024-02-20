package com.yaser.news.constant;

import lombok.Getter;

public enum ResultCode {
    SUCCESS(200, "成功!"),
    ERROR(500, "服务器内部错误!"),
    PERMISSION_DENY(1000, "用户权限不足！"),
    USER_PASSWORD_ERROR(1002, "用户密码错误！"),
    USER_NOT_EXIST_ERROR(1003, "用户不存在，请先注册！"),
    TOKEN_NOT_FOUND_UID(1004, "Token中未找到用户id！"),
    TOKEN_NOT_FOUND(1005, "请登录后再操作！"),
    VALIDATE_FAILED(1006, "参数校验失败"),
    TOKEN_EXPIRED(1007, "token已过期"),
    ILLEGAL_TOKEN(1008, "token是非法的"),
    USER_EXIST(1009, "用户已经存在了，请直接登录！"),
    CHANNEL_NOT_EXIST(1010, "当前请求的channel不存在！"),
    NEW_NOT_EXIST(1011, "新闻不存在！"),
    LABELS_TOO_MUCH(1013, "用户标签不应超过8个！"),
    LABEL_TOO_LONG(1014, "单个用户标签长度不可超过4个字符！"),
    USER_NEWS_SCORE_EXIST(1015, "用户已对该新闻评分过了！");

    @Getter
    private final int code;
    @Getter
    private final String message;

    ResultCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }


}
