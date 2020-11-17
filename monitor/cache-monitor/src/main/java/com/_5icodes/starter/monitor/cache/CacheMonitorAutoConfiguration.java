package com._5icodes.starter.monitor.cache;

import com._5icodes.starter.monitor.cache.serializer.RedisTemplateSerializerWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheMonitorAutoConfiguration {
    @Bean
    public RedisTemplateAspect redisTemplateAspect() {
        return new RedisTemplateAspect();
    }

    @Bean
    public RedisTemplateSerializerWrapper redisTemplateSerializerWrapper() {
        return new RedisTemplateSerializerWrapper();
    }
}