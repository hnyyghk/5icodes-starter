package com._5icodes.starter.sentinel.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import com._5icodes.starter.sentinel.SentinelConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KafkaSentinelMetricSender extends AbstractSentinelMetricSender {
    private final MonitorKafkaTemplate kafkaTemplate;

    public KafkaSentinelMetricSender(MonitorKafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    void doSend(Map<String, Object> sentinel) {
        sentinel.put("zone", RegionUtils.getZone());
        kafkaTemplate.send(SentinelConstants.SENTINEL_METRIC_TOPIC, JsonUtils.toJson(sentinel));
    }
}