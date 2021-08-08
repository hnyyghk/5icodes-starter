package com._5icodes.starter.web.log;

import com._5icodes.starter.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class AbstractAccessLog {
    @Autowired
    protected AccessLogSender accessLogSender;
    @Autowired
    private List<StaticAccessLogCollector> collectors;

    protected void sendAccessLog(Map<String, Object> accessLog) {
        for (StaticAccessLogCollector collector : collectors) {
            collector.collect(accessLog);
        }
        accessLogSender.doSend(JsonUtils.toJson(accessLog));
    }
}