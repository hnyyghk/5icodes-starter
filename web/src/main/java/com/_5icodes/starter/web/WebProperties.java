package com._5icodes.starter.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = WebConstants.COMMON_PREFIX)
public class WebProperties {
    private String accessLogTopic = WebConstants.ACCESS_LOG_TOPIC;

    private boolean accessLogEnabled = true;

    private boolean internalEnabled = true;

    private String contextPath;

    private int internalPort = 6088;
    /**
     * access日志版本
     */
    private String accessVersion = "1.0";
}