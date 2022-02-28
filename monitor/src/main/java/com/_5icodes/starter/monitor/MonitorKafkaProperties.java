package com._5icodes.starter.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = MonitorConstants.PROPERTY_PREFIX)
public class MonitorKafkaProperties extends KafkaProperties.Producer {
    private int ringBufferSize = 256 * 1024;

    private boolean enabled = true;
}