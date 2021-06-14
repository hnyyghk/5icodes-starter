package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.common.exception.CodeMsg;

public enum MockNotSupportCodeMsgEnums implements CodeMsg {
    MOCK_NOT_SUPPORT(10010, "%s");

    private final Integer code;
    private final String message;

    MockNotSupportCodeMsgEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}