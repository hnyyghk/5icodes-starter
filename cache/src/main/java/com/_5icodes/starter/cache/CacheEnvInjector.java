package com._5icodes.starter.cache;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

public class CacheEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        int nodes = 1;
        Binder binder = Binder.get(env);
        BindResult<RedisProperties> bindResult = binder.bind("spring.redis", Bindable.of(RedisProperties.class));
        if (bindResult.isBound()) {
            RedisProperties redisProperties = bindResult.get();
            RedisProperties.Cluster cluster = redisProperties.getCluster();
            if (null != cluster) {
                nodes = cluster.getNodes().size();
            }
        }
        PropertySourceUtils.put(env, "spring.redis.lettuce.pool.max-active", nodes * 20);
        PropertySourceUtils.put(env, "spring.redis.lettuce.pool.max-wait", "1s");
        PropertySourceUtils.put(env, "spring.redis.lettuce.pool.max-idle", nodes * 5);
        PropertySourceUtils.put(env, "spring.redis.lettuce.pool.min-idle", nodes * 2);
        PropertySourceUtils.put(env, "spring.redis.timeout", "1s");

        PropertySourceUtils.put(env, "jetcache.statIntervalMinutes", 15);
        PropertySourceUtils.put(env, "jetcache.areaInCacheName", false);
        PropertySourceUtils.put(env, "jetcache.local.default.type", "caffeine");
        PropertySourceUtils.put(env, "jetcache.local.default.limit", 200);
        PropertySourceUtils.put(env, "jetcache.local.default.keyConvertor", "jackson");

        PropertySourceUtils.put(env, "jetcache.remote.default.keyConvertor", "jackson");
        PropertySourceUtils.put(env, "jetcache.remote.default.type", "redis.springdata");
        PropertySourceUtils.put(env, "jetcache.remote.default.valueEncoder", "jackson");
        PropertySourceUtils.put(env, "jetcache.remote.default.valueDecoder", "jackson");
        PropertySourceUtils.put(env, "jetcache.remote.default.expireAfterWriteInMillis", 30 * 60 * 1000);
        super.onAllProfiles(env, application);
    }
}