package com._5icodes.starter.common.utils;

import org.slf4j.MDC;

public class TraceUtils {
    public static String getReqId() {
        return MDC.get("traceId");
    }
}