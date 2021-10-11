package com._5icodes.starter.monitor.cache.serializer;

import com._5icodes.starter.monitor.cache.CacheContext;
import com._5icodes.starter.monitor.cache.CacheOperationType;
import com._5icodes.starter.monitor.cache.key.CacheKeyUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CustomRedisSerializer<T> implements RedisSerializer<T> {
    private final RedisSerializer redisSerializer;

    public CustomRedisSerializer(RedisSerializer redisSerializer) {
        this.redisSerializer = redisSerializer;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        byte[] bytes = redisSerializer.serialize(t);
        CacheContext cacheContext = CacheKeyUtils.getCacheContext();
        if (null != cacheContext && CacheOperationType.SET.equals(cacheContext.getCacheOperationType())) {
            cacheContext.setValueSize(null == bytes ? 0 : bytes.length);
        }
        return bytes;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        T t = (T) redisSerializer.deserialize(bytes);
        CacheContext cacheContext = CacheKeyUtils.getCacheContext();
        if (null != cacheContext && CacheOperationType.GET.equals(cacheContext.getCacheOperationType())) {
            cacheContext.setValueSize(null == bytes ? 0 : bytes.length);
        }
        return t;
    }
}