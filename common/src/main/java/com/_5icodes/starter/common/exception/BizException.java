package com._5icodes.starter.common.exception;

import lombok.Getter;

public class BizException extends Exception implements CodeMsg {
    @Getter
    private final Integer code;

    public BizException(CodeMsg codeMsg, Object... arguments) {
        super(CodeMsgRegistry.getMsgByCode(codeMsg.getCode(), arguments));
        this.code = codeMsg.getCode();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}