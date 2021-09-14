package com._5icodes.starter.web.monitor;

public interface AccessLogSender {
    void doSend(String metricStr);
}