package com._5icodes.starter.monitor;

import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.utils.*;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExceptionReport {
    @Autowired
    private CurrentTraceContext currentTraceContext;

    public boolean report(String resource, Throwable throwable) {
        Throwable realException = ExceptionUtils.getRealException(throwable);
        if (realException == null) {
            return false;
        }
        if (realException instanceof CodeMsg) {
            return false;
        }
        Map<String, Object> exceptionMap = new HashMap<>();
        exceptionMap.put("app", SpringApplicationUtils.getApplicationName());
        exceptionMap.put("ip", HostNameUtils.getHostAddress());
        exceptionMap.put("resource", resource);
        exceptionMap.put("exception", realException.getClass().getSimpleName());
        exceptionMap.put("test", TraceTestUtils.isTraceTest());
        exceptionMap.put("zone", RegionUtils.getZone());
        String msg = realException.getMessage();
        if (msg != null) {
            int maxLength = 1000;
            if (msg.length() > maxLength) {
                msg = msg.substring(0, maxLength);
            }
            exceptionMap.put("msg", msg);
        }
        exceptionMap.put("time", System.currentTimeMillis());
        exceptionMap.putAll(getTraceMap());
        //todo
        log.info(JsonUtils.toJson(exceptionMap));
        return true;
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