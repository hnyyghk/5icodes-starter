package com._5icodes.starter.stress.cache.test;

import com.alicp.jetcache.external.ExternalCacheBuilder;
import com.alicp.jetcache.redis.springdata.RedisSpringDataCacheBuilder;

public class TraceTestRedisSpringDataCacheBuilder<T extends ExternalCacheBuilder<T>> extends RedisSpringDataCacheBuilder<T> {
    @Override
    public TraceTestRedisSpringDataCacheConfig getConfig() {
        if (config == null) {
            config = new TraceTestRedisSpringDataCacheConfig();
        }
        return (TraceTestRedisSpringDataCacheConfig) config;
    }
}