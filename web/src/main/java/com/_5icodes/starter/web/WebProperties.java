package com._5icodes.starter.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = WebConstants.COMMON_PREFIX)
public class WebProperties {
    /**
     * access日志版本
     */
    private String accessVersion = "1.0";
}