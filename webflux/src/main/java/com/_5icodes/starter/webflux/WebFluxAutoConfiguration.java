package com._5icodes.starter.webflux;

import brave.propagation.CurrentTraceContext;
import com._5icodes.starter.monitor.ExceptionReport;
import com._5icodes.starter.web.WebProperties;
import com._5icodes.starter.web.condition.ConditionalOnAccessLog;
import com._5icodes.starter.webflux.monitor.ExceptionReportHandler;
import com._5icodes.starter.webflux.monitor.WebFluxAccessLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class WebFluxAutoConfiguration {
    @Bean
    @ConditionalOnAccessLog
    public WebFluxAccessLog webFluxAccessLog(WebProperties webProperties, CurrentTraceContext currentTraceContext) {
        return new WebFluxAccessLog(webProperties, currentTraceContext);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ExceptionReportHandler exceptionReportHandler(ExceptionReport exceptionReport) {
        return new ExceptionReportHandler(exceptionReport);
    }
}