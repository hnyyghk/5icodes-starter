package com._5icodes.starter.stress.cache.serializer;

import com._5icodes.starter.stress.cache.test.TraceTestKeySerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateKeySerializerWrapper implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RedisTemplate) {
            RedisTemplate redisTemplate = (RedisTemplate) bean;
            redisTemplate.setKeySerializer(new TraceTestKeySerializer(redisTemplate.getKeySerializer()));
            redisTemplate.setHashKeySerializer(new TraceTestKeySerializer(redisTemplate.getHashKeySerializer()));
        }
        return bean;
    }
}