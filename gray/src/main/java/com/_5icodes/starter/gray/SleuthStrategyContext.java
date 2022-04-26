package com._5icodes.starter.gray;

import org.springframework.core.NamedThreadLocal;

public class SleuthStrategyContext {
    private static final ThreadLocal<Boolean> THREAD_LOCAL = new NamedThreadLocal<>("sleuthStrategyContext");

    public static void set(boolean value) {
        if (value) {
            THREAD_LOCAL.set(Boolean.TRUE);
        }
    }

    public static boolean get() {
        return THREAD_LOCAL.get() == null ? Boolean.FALSE : Boolean.TRUE;
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}