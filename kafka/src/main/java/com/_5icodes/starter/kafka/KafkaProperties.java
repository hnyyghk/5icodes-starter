package com._5icodes.starter.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = KafkaConstants.PROPERTY_PREFIX)
public class KafkaProperties {
    private List<String> grayTopics;
}