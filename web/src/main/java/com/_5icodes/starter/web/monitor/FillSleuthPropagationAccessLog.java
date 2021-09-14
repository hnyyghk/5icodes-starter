package com._5icodes.starter.web.monitor;

import brave.baggage.BaggageField;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import com._5icodes.starter.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class FillSleuthPropagationAccessLog extends AbstractAccessLog {
    @Autowired
    private CurrentTraceContext currentTraceContext;

    @Override
    protected void sendAccessLog(Map<String, Object> accessLog) {
        Map<String, Object> spec = new HashMap<>();
        Map<String, String> propagation = BaggageField.getAllValues();
        String clientIp = propagation.get(WebConstants.CLIENT_IP);
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