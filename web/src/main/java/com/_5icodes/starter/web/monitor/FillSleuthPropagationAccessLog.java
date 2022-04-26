package com._5icodes.starter.web.monitor;

import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import com._5icodes.starter.sleuth.SleuthConstants;
import com._5icodes.starter.sleuth.utils.BaggageFieldUtils;

import java.util.HashMap;
import java.util.Map;

public class FillSleuthPropagationAccessLog extends AbstractAccessLog {
    private final CurrentTraceContext currentTraceContext;

    public FillSleuthPropagationAccessLog(CurrentTraceContext currentTraceContext) {
        this.currentTraceContext = currentTraceContext;
    }

    @Override
    protected void sendAccessLog(Map<String, Object> accessLog) {
        Map<String, Object> spec = new HashMap<>();
        String clientIp = BaggageFieldUtils.get(SleuthConstants.CLIENT_IP);
        spec.put("clientIp", clientIp != null ? clientIp : "");
        //todo
        spec.put("trace", getTraceMap());
        accessLog.put("spec", spec);
        super.sendAccessLog(accessLog);
    }

    private Map<String, Object> getTraceMap() {
        Map<String, Object> trace = new HashMap<>();
        TraceContext traceContext = currentTraceContext.get();
        if (traceContext != null) {
            trace.put("spanId", traceContext.spanIdString());
            trace.put("parentId", traceContext.parentIdString());
            trace.put("traceId", traceContext.traceIdString());
        } else {
            trace.put("spanId", "");
            trace.put("parentId", "");
            trace.put("traceId", "");
        }
        return trace;
    }
}