package com._5icodes.starter.async;

import com._5icodes.starter.rocketmq.RocketmqProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = AsyncConstants.PROPERTY_PREFIX)
@Data
public class AsyncProperties extends RocketmqProperties.Consumer {
    private RocketmqProperties.Consumer order;
}