package com._5icodes.starter.cache.test;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class RedisConnectFactoryLifecycle extends AbstractSmartLifecycle {
    private final LettuceConnectionFactory connectionFactory;

    public RedisConnectFactoryLifecycle(LettuceConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void doStart() {
    }

    @Override
    public void doStop() {
        connectionFactory.destroy();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}