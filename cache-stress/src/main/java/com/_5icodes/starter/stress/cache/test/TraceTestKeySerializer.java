package com._5icodes.starter.stress.cache.test;

import com._5icodes.starter.stress.cache.CacheStressConstants;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class TraceTestKeySerializer<T> implements RedisSerializer<T> {
    private static final byte[] CACHE_PREFIX_BYTES = CacheStressConstants.CACHE_PREFIX.getBytes();
    private static final int CACHE_PREFIX_BYTES_LENGTH = CACHE_PREFIX_BYTES.length;
    private final RedisSerializer redisSerializer;

    public TraceTestKeySerializer(RedisSerializer redisSerializer) {
        if (null == redisSerializer) {
            this.redisSerializer = new StringRedisSerializer();
        } else {
            this.redisSerializer = redisSerializer;
        }
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        byte[] bytes = redisSerializer.serialize(t);
        if (TraceTestUtils.isTraceTest()) {
            TraceTestUtils.info("this is trace test cache key: {}", CacheStressConstants.CACHE_PREFIX + t);
            return ArrayUtils.addAll(CACHE_PREFIX_BYTES, bytes);
        }
        return bytes;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (TraceTestUtils.isTraceTest()) {
            TraceTestUtils.info("this key is trace test split: {}", bytes);
            bytes = ArrayUtils.subarray(bytes, CACHE_PREFIX_BYTES_LENGTH, bytes.length);
        }
        return (T) redisSerializer.deserialize(bytes);
    }
}