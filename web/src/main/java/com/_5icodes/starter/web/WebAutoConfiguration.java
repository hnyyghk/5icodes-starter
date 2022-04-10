package com._5icodes.starter.web;

import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import com._5icodes.starter.web.condition.ConditionalOnAccessLog;
import com._5icodes.starter.web.condition.ConditionalOnInternalServer;
import com._5icodes.starter.web.internal.*;
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
    public AccessLogSender accessLogSender(WebProperties properties, MonitorKafkaTemplate kafkaTemplate) {
        return new KafkaAccessLogSender(properties.getAccessLogTopic(), kafkaTemplate);
    }

    @Configuration
    @ConditionalOnInternalServer
    @ConditionalOnClass(ApplicationInfoManager.class)
    public static class RegistryAccessLogAutoConfiguration {
        @Bean
        @ConditionalOnBean(EurekaClient.class)
        public GracefulShutdownHandler gracefulShutdownHandler(EurekaClient discoveryClient) {
            return new GracefulShutdownHandler(discoveryClient);
        }
    }

    @Configuration
    @ConditionalOnInternalServer
    public static class InternalServerAutoConfiguration {
        @Bean
        public HealthProbeHandler healthProbeHandler() {
            return new HealthProbeHandler();
        }

        @Bean
        public DumpHandler dumpHandler() {
            return new DumpHandler();
        }

        @Bean
        public InternalServer internalServer(List<InternalHandler> internalHandlers, WebProperties properties) {
            return new InternalServer(internalHandlers, properties);
        }
    }
}