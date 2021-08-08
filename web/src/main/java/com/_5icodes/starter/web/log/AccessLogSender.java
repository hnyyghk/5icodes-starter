package com._5icodes.starter.web.log;

public interface AccessLogSender {
    void doSend(String metricStr);
}