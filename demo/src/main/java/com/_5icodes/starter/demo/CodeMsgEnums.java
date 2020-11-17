package com._5icodes.starter.demo;

import com._5icodes.starter.common.exception.CodeMsg;

public enum CodeMsgEnums implements CodeMsg {
    FRAME_ERROR(-2, "服务器异常"),
    NULL(1000, "%s不能为空"),
    TOKEN_OVERTIME(2000, "token超时"),
    REPEAT(2001, "%s重复"),
    SELF_CONFIG_ERROR(9998, "%s");

    private final Integer code;
    private final String message;

    CodeMsgEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}