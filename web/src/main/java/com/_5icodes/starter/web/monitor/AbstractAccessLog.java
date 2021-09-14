package com._5icodes.starter.web.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractAccessLog {
    @Autowired
    protected AccessLogSender accessLogSender;

    protected void sendAccessLog(Map<String, Object> accessLog) {
        accessLogSender.doSend(JsonUtils.toJson(accessLog));
    }
}