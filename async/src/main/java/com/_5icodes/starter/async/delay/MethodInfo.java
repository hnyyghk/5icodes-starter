package com._5icodes.starter.async.delay;

import lombok.Data;

import java.io.Serializable;

@Data
public class MethodInfo implements Serializable {
    private String methodName;
    private Class<?>[] parameterTypes;
    private Class<?> declaringClass;
}