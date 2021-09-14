package com._5icodes.starter.webflux;

import com._5icodes.starter.web.WebProperties;
import com._5icodes.starter.webflux.monitor.ExceptionReportHandler;
import com._5icodes.starter.webflux.monitor.WebFluxAccessLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class WebFluxAutoConfiguration {
    @Bean
    public WebFluxAccessLog webFluxAccessLog(WebProperties webProperties) {
        return new WebFluxAccessLog(webProperties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ExceptionReportHandler exceptionReportHandler() {
        return new ExceptionReportHandler();
    }
}