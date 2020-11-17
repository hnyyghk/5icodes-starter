package com._5icodes.starter.stress.cache;

import com._5icodes.starter.stress.cache.serializer.RedisTemplateKeySerializerWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheStressAutoConfiguration {
    @Bean
    public RedisTemplateKeySerializerWrapper redisTemplateKeySerializerWrapper() {
        return new RedisTemplateKeySerializerWrapper();
    }
}