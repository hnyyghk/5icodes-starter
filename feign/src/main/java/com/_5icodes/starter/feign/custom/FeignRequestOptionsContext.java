package com._5icodes.starter.feign.custom;

import feign.Request;
import org.springframework.core.NamedThreadLocal;

public class FeignRequestOptionsContext {
    private static final ThreadLocal<Request.Options> THREAD_LOCAL = new NamedThreadLocal<>("feignCustom");

    public static void set(Request.Options options) {
        THREAD_LOCAL.set(options);
    }

    public static Request.Options get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}