package com._5icodes.starter.gray;

import org.springframework.core.NamedThreadLocal;

public class SleuthStrategyContext {
    private static final ThreadLocal<Boolean> THREAD_LOCAL = new NamedThreadLocal<>("sleuthStrategyContext");

    public static void set(Boolean value) {
        THREAD_LOCAL.set(value);
    }

    public static Boolean get() {
        Boolean value = THREAD_LOCAL.get();
        return value == null ? Boolean.FALSE : value;
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}