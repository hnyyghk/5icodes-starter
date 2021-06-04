package com._5icodes.starter.monitor.cache.serializer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateSerializerWrapper implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RedisTemplate) {
            RedisTemplate redisTemplate = (RedisTemplate) bean;
            redisTemplate.setValueSerializer(new CustomRedisSerializer(redisTemplate.getValueSerializer()));
        }
        return bean;
    }
}