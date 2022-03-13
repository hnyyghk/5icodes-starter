package com._5icodes.starter.async.registry;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncRegistry {
    private final Map<Method, AsyncRetryProperties> CACHE = new ConcurrentHashMap<>();

    public void register(Method method, AsyncRetryProperties properties) {
        CACHE.put(method, properties);
    }

    public AsyncRetryProperties get(Method method) {
        return CACHE.get(method);
    }
}