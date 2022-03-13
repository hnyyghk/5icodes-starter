package com._5icodes.starter.async.delay;

import com._5icodes.starter.async.AsyncContext;
import lombok.Data;

import java.io.Serializable;

@Data
public class AsyncSerializableObj implements Serializable {
    private AsyncContext context;
    private MethodInfo methodInfo;
}