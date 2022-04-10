package com._5icodes.starter.web.monitor;

import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaAccessLogSender implements AccessLogSender {
    private final String topic;
    private final MonitorKafkaTemplate kafkaTemplate;

    public KafkaAccessLogSender(String topic, MonitorKafkaTemplate kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void doSend(String metricStr) {
        kafkaTemplate.send(topic, metricStr);
    }
}