package com._5icodes.starter.feign.auth;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class BasicAuthConfiguration {
    @Bean
    public RequestInterceptor basicInterceptor(BasicAuthParameterProcessor basicAuthParameterProcessor) {
        return basicAuthParameterProcessor.getAuthInterceptor();
    }
}