package com._5icodes.starter.web.log;

import java.util.Map;

public interface StaticAccessLogCollector {
    void collect(Map<String, Object> accessLog);
}