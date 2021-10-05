package com._5icodes.starter.web.monitor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaAccessLogSender implements AccessLogSender {
    private final String topic;

    public KafkaAccessLogSender(String topic) {
        this.topic = topic;
    }

    @Override
    public void doSend(String metricStr) {
        //todo
        log.info("{}: {}", topic, metricStr);
    }
}