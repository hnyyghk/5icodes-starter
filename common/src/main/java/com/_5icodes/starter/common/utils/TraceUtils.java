package com._5icodes.starter.common.utils;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class TraceUtils {
    @Autowired
    private Tracing tracing;

    private static Tracer staticTracer;

    @PostConstruct
    public void init() {
        staticTracer = tracing.tracer();
    }

    public static ScopedSpan span(String name) {
        return staticTracer.startScopedSpan(name);
    }

    public static String getReqId() {
        return MDC.get("traceId");
    }
}