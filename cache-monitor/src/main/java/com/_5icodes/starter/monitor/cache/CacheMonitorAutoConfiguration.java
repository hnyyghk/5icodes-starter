package com._5icodes.starter.monitor.cache;

import com._5icodes.starter.monitor.cache.monitor.CacheMetricReporter;
import com._5icodes.starter.monitor.cache.serializer.RedisTemplateSerializerWrapper;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheMonitorAutoConfiguration {
    @Bean
    public RedisTemplateAspect redisTemplateAspect() {
        return new RedisTemplateAspect();
    }

    @Bean
    public CacheMetricReporter cacheMetricReporter() {
        return new CacheMetricReporter();
    }

    @Bean
    public RedisTemplateSerializerWrapper redisTemplateSerializerWrapper() {
        return new RedisTemplateSerializerWrapper();
    }

    @Bean(destroyMethod = "shutdown")
    public DefaultClientResources lettuceClientResources() {
        DefaultEventPublisherOptions eventPublisherOptions = DefaultEventPublisherOptions.builder().eventEmitInterval(Duration.ofMinutes(1)).build();
        return DefaultClientResources.builder().commandLatencyPublisherOptions(eventPublisherOptions).build();
    }

    @Bean
    public LettuceEventConsumer lettuceEventConsumer(DefaultClientResources clientResources) {
        return new LettuceEventConsumer(clientResources);
    }
}