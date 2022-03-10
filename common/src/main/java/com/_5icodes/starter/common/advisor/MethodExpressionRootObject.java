package com._5icodes.starter.common.advisor;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class MethodExpressionRootObject {
    private final Method method;
    private final Object[] arguments;
    private final Object target;
    private final Class<?> targetClass;

    public MethodExpressionRootObject(Method method, Object[] arguments, Object target, Class<?> targetClass) {
        this.method = method;
        this.arguments = arguments;
        this.target = target;
        this.targetClass = targetClass;
    }
}