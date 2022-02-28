package com._5icodes.starter.kafka;

import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.kafka.trace.TraceTestListenerBeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @see org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
 * @see org.springframework.boot.autoconfigure.kafka.KafkaAnnotationDrivenConfiguration
 * @see org.springframework.cloud.sleuth.autoconfig.brave.instrument.messaging.BraveMessagingAutoConfiguration.SleuthKafkaConfiguration
 */
@Configuration
@EnableKafka
@AutoConfigureAfter(org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {
    @Bean
    public TraceTestListenerBeanPostProcessor traceTestListenerBeanPostProcessor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider, KafkaProperties properties) {
        return new TraceTestListenerBeanPostProcessor(metadataReaderFactoryProvider, properties);
    }
}