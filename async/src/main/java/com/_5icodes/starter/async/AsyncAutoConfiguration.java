package com._5icodes.starter.async;

import com._5icodes.starter.async.annotation.AsyncInterceptor;
import com._5icodes.starter.async.annotation.AsyncRunAdvisor;
import com._5icodes.starter.async.codec.AsyncCodec;
import com._5icodes.starter.async.codec.AsyncKryoCodec;
import com._5icodes.starter.async.consumer.AsyncMessageListenerConcurrently;
import com._5icodes.starter.async.consumer.AsyncMessageListenerOrderly;
import com._5icodes.starter.async.consumer.AsyncRocketmqMessageListener;
import com._5icodes.starter.async.delay.Delayer;
import com._5icodes.starter.async.delay.AsyncRocketmqDelayer;
import com._5icodes.starter.async.operations.AsyncOperations;
import com._5icodes.starter.async.operations.AsyncTemplate;
import com._5icodes.starter.async.registry.AsyncRegistry;
import com._5icodes.starter.async.configuration.ConditionalOnConcurrently;
import com._5icodes.starter.async.configuration.ConditionalOnOrderly;
import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.rocketmq.RocketmqAutoConfiguration;
import com.esotericsoftware.kryo.Kryo;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@AutoConfigureAfter({RocketmqAutoConfiguration.class, BraveAutoConfiguration.class})
@EnableConfigurationProperties(AsyncProperties.class)
public class AsyncAutoConfiguration {
    @ConditionalOnClass(Kryo.class)
    @Configuration
    public static class AsyncKryoCodecAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public AsyncCodec asyncCodec() {
            return new AsyncKryoCodec();
        }
    }

    @Bean
    public Delayer delayer(AsyncProperties asyncProperties, DefaultMQProducer defaultMqProducer, AsyncCodec asyncCodec) {
        return new AsyncRocketmqDelayer(asyncProperties, defaultMqProducer, asyncCodec);
    }

    @Bean
    public AsyncRegistry asyncRegistry() {
        return new AsyncRegistry();
    }

    @Bean
    public AsyncOperations asyncOperations(AsyncRegistry registry, Delayer delayer) {
        return new AsyncTemplate(registry, delayer);
    }

    @Bean(AsyncConstants.ASYNC_INTERCEPTOR_BEAN_NAME)
    public AsyncInterceptor asyncInterceptor(AsyncOperations asyncOperations) {
        return new AsyncInterceptor(asyncOperations);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AsyncRunAdvisor asyncRunAdvisor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider, AsyncProperties asyncProperties, AsyncRegistry asyncRegistry) {
        return new AsyncRunAdvisor(metadataReaderFactoryProvider, asyncProperties, asyncRegistry);
    }

    @Bean
    public AsyncRocketmqMessageListener asyncRocketmqMessageListener(AsyncCodec asyncCodec, AsyncOperations asyncOperations) {
        return new AsyncRocketmqMessageListener(asyncCodec, asyncOperations);
    }

    @Bean(AsyncConstants.MESSAGE_LISTENER_CONCURRENTLY_BEAN_NAME)
    @ConditionalOnConcurrently
    public AsyncMessageListenerConcurrently asyncMessageListenerConcurrently(AsyncRocketmqMessageListener asyncRocketmqMessageListener) {
        return new AsyncMessageListenerConcurrently(asyncRocketmqMessageListener);
    }

    @Bean(AsyncConstants.MESSAGE_LISTENER_ORDERLY_BEAN_NAME)
    @ConditionalOnOrderly
    public AsyncMessageListenerOrderly asyncMessageListenerOrderly(AsyncRocketmqMessageListener asyncRocketmqMessageListener) {
        return new AsyncMessageListenerOrderly(asyncRocketmqMessageListener);
    }
}