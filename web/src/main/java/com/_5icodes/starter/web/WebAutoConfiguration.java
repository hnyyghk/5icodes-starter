package com._5icodes.starter.web;

import com._5icodes.starter.web.condition.ConditionalOnAccessLog;
import com._5icodes.starter.web.condition.ConditionalOnInternalServer;
import com._5icodes.starter.web.internal.DumpHandler;
import com._5icodes.starter.web.internal.GracefulShutdownHandler;
import com._5icodes.starter.web.internal.InternalHandler;
import com._5icodes.starter.web.internal.InternalServer;
import com._5icodes.starter.web.monitor.AccessLogSender;
import com._5icodes.starter.web.monitor.KafkaAccessLogSender;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration {
    @Bean
    @ConditionalOnAccessLog
    public AccessLogSender accessLogSender(WebProperties properties) {
        return new KafkaAccessLogSender(properties.getAccessLogTopic());
    }

    @Configuration
    @ConditionalOnInternalServer
    public static class InternalServerAutoConfiguration {
        @Bean
        public DumpHandler dumpHandler() {
            return new DumpHandler();
        }

        @Bean
        public InternalServer internalServer(List<InternalHandler> internalHandlers, WebProperties properties) {
            return new InternalServer(internalHandlers, properties.getInternalPort());
        }

        @Bean
        @ConditionalOnClass(ApplicationInfoManager.class)
        @ConditionalOnBean(EurekaClient.class)
        public GracefulShutdownHandler gracefulShutdownHandler(EurekaClient discoveryClient) {
            return new GracefulShutdownHandler(discoveryClient);
        }
    }
}