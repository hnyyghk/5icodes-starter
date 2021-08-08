package com._5icodes.starter.web.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaAccessLogSender implements AccessLogSender {
    @Override
    public void doSend(String metricStr) {
        //todo
        log.info("{}", metricStr);
    }
}