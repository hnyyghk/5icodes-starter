package com._5icodes.starter.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorAutoConfiguration {
    @Bean
    public ExceptionReport exceptionReport() {
        return new ExceptionReport();
    }
}