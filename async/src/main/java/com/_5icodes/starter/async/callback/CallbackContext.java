package com._5icodes.starter.async.callback;

import com._5icodes.starter.async.AsyncContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CallbackContext {
    private final ThreadLocal<AsyncContext> CALLBACK_THREAD_LOCAL = new ThreadLocal<>();

    public void set(AsyncContext context) {
        CALLBACK_THREAD_LOCAL.set(context);
    }

    public AsyncContext get() {
        return CALLBACK_THREAD_LOCAL.get();
    }

    public void remove() {
        CALLBACK_THREAD_LOCAL.remove();
    }
}