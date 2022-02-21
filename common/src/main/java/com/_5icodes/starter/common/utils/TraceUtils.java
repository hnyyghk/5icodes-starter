package com._5icodes.starter.common.utils;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;

public class TraceUtils {
    private final Tracing tracing;

    private static Tracer staticTracer;

    public TraceUtils(Tracing tracing) {
        this.tracing = tracing;
    }

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