package com._5icodes.starter.web;

import com._5icodes.starter.web.monitor.AccessLogSender;
import com._5icodes.starter.web.monitor.KafkaAccessLogSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration {
    @Bean
    public AccessLogSender accessLogSender() {
        return new KafkaAccessLogSender();
    }
}