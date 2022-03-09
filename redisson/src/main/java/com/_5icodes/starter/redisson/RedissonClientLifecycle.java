package com._5icodes.starter.redisson;

import org.redisson.api.RedissonClient;
import org.springframework.context.SmartLifecycle;

public class RedissonClientLifecycle implements SmartLifecycle {
    private final RedissonClient redissonClient;

    public RedissonClientLifecycle(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        redissonClient.shutdown();
    }

    @Override
    public boolean isRunning() {
        return !redissonClient.isShutdown();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}