package com._5icodes.starter.saturn;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "saturn")
public class SaturnProperties {
    private boolean enabled = true;
    private String namespace;
    private String executorName;
    private String consoleAddress;
}