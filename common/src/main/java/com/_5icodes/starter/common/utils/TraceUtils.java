package com._5icodes.starter.common.utils;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import org.slf4j.MDC;

public class TraceUtils {
    private static Tracer staticTracer;

    public TraceUtils(Tracing tracing) {
        staticTracer = tracing.tracer();
    }

    public static ScopedSpan span(String name) {
        return staticTracer.startScopedSpan(name);
    }

    public static String getReqId() {
        return MDC.get("traceId");
    }
}