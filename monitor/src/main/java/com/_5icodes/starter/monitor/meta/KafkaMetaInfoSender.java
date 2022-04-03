package com._5icodes.starter.monitor.meta;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.monitor.MonitorKafkaProperties;
import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KafkaMetaInfoSender extends AbstractMetaInfoSender {
    private final MonitorKafkaTemplate monitorKafkaTemplate;
    private final MonitorKafkaProperties monitorKafkaProperties;

    public KafkaMetaInfoSender(MonitorKafkaTemplate monitorKafkaTemplate, MonitorKafkaProperties monitorKafkaProperties) {
        this.monitorKafkaTemplate = monitorKafkaTemplate;
        this.monitorKafkaProperties = monitorKafkaProperties;
    }

    @Override
    public void doSend(Map<String, Object> metaInfo) {
        monitorKafkaTemplate.send(monitorKafkaProperties.getMetricTopicName(), JsonUtils.toJson(metaInfo));
    }
}