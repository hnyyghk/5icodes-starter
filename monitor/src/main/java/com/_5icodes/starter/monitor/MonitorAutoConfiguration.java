package com._5icodes.starter.monitor;

import com._5icodes.starter.monitor.meta.KafkaMetaInfoSender;
import com._5icodes.starter.monitor.meta.MetaInfoSender;
import com._5icodes.starter.monitor.meta.MetaInfoSenderApplicationEventAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorAutoConfiguration {
    @Bean
    public MetaInfoSenderApplicationEventAdapter metaInfoSenderApplicationEventAdapter() {
        return new MetaInfoSenderApplicationEventAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaInfoSender kafkaMetaInfoSender() {
        return new KafkaMetaInfoSender();
    }

    @Bean
    public ExceptionReport exceptionReport() {
        return new ExceptionReport();
    }
}