package com._5icodes.starter.common.exception;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class CodeMsgRegistry {
    private static final Map<Integer, CodeMsg> REGISTRY = new HashMap<>();

    public static void register(CodeMsg codeMsg) {
        Object previous = REGISTRY.putIfAbsent(codeMsg.getCode(), codeMsg);
        if (null != previous) {
            throw new IllegalArgumentException(String.format("code %d is registered by %s, registration by %s is failed", codeMsg.getCode(), previous.getClass().getName(), codeMsg.getClass().getName()));
        }
    }

    public static void deRegister(CodeMsg codeMsg) {
        boolean remove = REGISTRY.remove(codeMsg.getCode(), codeMsg);
        if (!remove) {
            CodeMsg previous = REGISTRY.get(codeMsg.getCode());
            if (ObjectUtils.isEmpty(previous)) {
                throw new IllegalArgumentException(String.format("code %d is not registered yet", codeMsg.getCode()));
            } else {
                throw new IllegalArgumentException(String.format("code %d is registered by %s, deRegistration by %s is failed", codeMsg.getCode(), previous.getClass().getName(), codeMsg.getClass().getName()));
            }
        }
    }

    public static String getMsgByCode(Integer code, Object... arguments) {
        CodeMsg codeMsg = REGISTRY.get(code);
        if (ObjectUtils.isEmpty(codeMsg)) {
            throw new IllegalArgumentException(String.format("code %d is not registered yet", code));
        }
        return String.format(codeMsg.getMessage(), arguments);
    }
}