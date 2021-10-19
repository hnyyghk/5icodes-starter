package com._5icodes.starter.cache.test;

import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class RedisConnectFactoryLifecycle implements SmartLifecycle {
    private final LettuceConnectionFactory connectionFactory;

    public RedisConnectFactoryLifecycle(LettuceConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        connectionFactory.destroy();
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}