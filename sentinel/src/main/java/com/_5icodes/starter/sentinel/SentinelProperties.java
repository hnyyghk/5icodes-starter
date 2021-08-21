package com._5icodes.starter.sentinel;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SentinelConstants.PROPERTY_PREFIX)
public class SentinelProperties {
    private boolean enabled = true;
}