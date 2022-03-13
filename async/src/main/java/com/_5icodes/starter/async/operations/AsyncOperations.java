package com._5icodes.starter.async.operations;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.registry.AsyncRetryProperties;

import java.lang.reflect.Method;

public interface AsyncOperations {
    Object runAndCheckAsync(AsyncContext context) throws Throwable;

    Object runAndRetryAsyncIfFailed(AsyncContext context) throws Throwable;

    void runAsync(AsyncContext context) throws Throwable;

    void register(Method method, AsyncRetryProperties properties);

    void runInCallback(AsyncContext context) throws Throwable;
}