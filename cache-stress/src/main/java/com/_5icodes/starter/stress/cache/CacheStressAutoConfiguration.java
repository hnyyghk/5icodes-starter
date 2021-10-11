package com._5icodes.starter.stress.cache;

import com._5icodes.starter.stress.cache.serializer.RedisTemplateKeySerializerWrapper;
import com._5icodes.starter.stress.cache.test.TraceTestRedisSpringDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TraceTestRedisSpringDataAutoConfiguration.class)
public class CacheStressAutoConfiguration {
    @Bean
    public RedisTemplateKeySerializerWrapper redisTemplateKeySerializerWrapper() {
        return new RedisTemplateKeySerializerWrapper();
    }
}