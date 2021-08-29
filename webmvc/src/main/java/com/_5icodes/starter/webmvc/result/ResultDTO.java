package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import com._5icodes.starter.common.utils.SpringUtils;
import com._5icodes.starter.webmvc.properties.SuccessProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ResultDTO<T> {
    private String reqId;
    private final Integer code;
    private final String message;
    private T data;

    public static <T> ResultDTO<T> setBack(CodeMsg codeMsg, Object... arguments) {
        return new ResultDTO<>(codeMsg, arguments);
    }

    public static <T> ResultDTO<T> setBackWithData(T data, CodeMsg codeMsg, Object... arguments) {
        return new ResultDTO<>(data, codeMsg, arguments);
    }

    public static <T> ResultDTO<T> setBack() {
        return new ResultDTO<>();
    }

    public static <T> ResultDTO<T> setBackWithData(T data) {
        return new ResultDTO<>(data);
    }

    private ResultDTO(CodeMsg codeMsg, Object... arguments) {
        this.message = CodeMsgRegistry.getMsgByCode(codeMsg.getCode(), arguments);
        this.code = codeMsg.getCode();
    }

    private ResultDTO(T data, CodeMsg codeMsg, Object... arguments) {
        this(codeMsg, arguments);
        this.data = data;
    }

    private ResultDTO() {
        this(SpringUtils.getBean(SuccessProperties.class));
    }

    private ResultDTO(T data) {
        this();
        this.data = data;
    }

    protected ResultDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    protected ResultDTO<T> setReqId(String reqId) {
        this.reqId = reqId;
        return this;
    }

    protected ResultDTO<T> setData(T data) {
        this.data = data;
        return this;
    }
}