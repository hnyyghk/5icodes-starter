package com._5icodes.starter.cache;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CacheEnvInjectorTest {
    @Test
    public void onAllProfiles() {
        CacheEnvInjector cacheEnvInjector = new CacheEnvInjector();
        SpringApplication application = Mockito.mock(SpringApplication.class);
        StandardEnvironment environment = new StandardEnvironment();
        Map<String, Object> params = new HashMap<>();
        environment.getPropertySources().addFirst(new MapPropertySource("test", params));
        cacheEnvInjector.onAllProfiles(environment, application);
        RedisProperties redisProperties = bind(environment);
        Assert.assertEquals(Duration.ofSeconds(1), redisProperties.getTimeout());
        RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
        RedisProperties.Pool pool = lettuce.getPool();
        Assert.assertEquals(Duration.ofSeconds(1), pool.getMaxWait());
        Assert.assertEquals(2, pool.getMinIdle());
        Assert.assertEquals(5, pool.getMaxIdle());
        Assert.assertEquals(20, pool.getMaxActive());

        params.put("spring.redis.cluster.nodes", "localhost:6379,localhost:6378");
        cacheEnvInjector.onAllProfiles(environment, application);
        redisProperties = bind(environment);
        lettuce = redisProperties.getLettuce();
        pool = lettuce.getPool();
        Assert.assertEquals(2 * 2, pool.getMinIdle());
        Assert.assertEquals(2 * 5, pool.getMaxIdle());
        Assert.assertEquals(2 * 20, pool.getMaxActive());

        params.clear();
        params.put("spring.redis.cluster.nodes[0]", "localhost:6375");
        params.put("spring.redis.cluster.nodes[1]", "localhost:6376");
        params.put("spring.redis.cluster.nodes[2]", "localhost:6377");
        cacheEnvInjector.onAllProfiles(environment, application);
        redisProperties = bind(environment);
        lettuce = redisProperties.getLettuce();
        pool = lettuce.getPool();
        Assert.assertEquals(3 * 2, pool.getMinIdle());
        Assert.assertEquals(3 * 5, pool.getMaxIdle());
        Assert.assertEquals(3 * 20, pool.getMaxActive());
    }

    private RedisProperties bind(StandardEnvironment environment) {
        Binder binder = Binder.get(environment);
        BindResult<RedisProperties> bindResult = binder.bind("spring.redis", Bindable.of(RedisProperties.class));
        Assert.assertTrue(bindResult.isBound());
        return bindResult.get();
    }
}