package com._5icodes.starter.stress.cache.test;

import com._5icodes.starter.stress.cache.CacheStressConstants;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import com.alicp.jetcache.redis.springdata.RedisSpringDataCacheConfig;

public class TraceTestRedisSpringDataCacheConfig<K, V> extends RedisSpringDataCacheConfig<K, V> {
    @Override
    public String getKeyPrefix() {
        String keyPrefix = super.getKeyPrefix();
        if (TraceTestUtils.isTraceTest()) {
            return keyPrefix == null ? CacheStressConstants.CACHE_STRESS_PREFIX : CacheStressConstants.CACHE_STRESS_PREFIX + keyPrefix;
        } else {
            return keyPrefix;
        }
    }

    @Override
    public void setKeyPrefix(String keyPrefix) {
        if (TraceTestUtils.isTraceTest()) {
            super.setKeyPrefix(keyPrefix.substring(CacheStressConstants.CACHE_STRESS_PREFIX.length()));
        } else {
            super.setKeyPrefix(keyPrefix);
        }
    }
}