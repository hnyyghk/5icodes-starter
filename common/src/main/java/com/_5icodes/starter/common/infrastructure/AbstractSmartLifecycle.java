package com._5icodes.starter.common.infrastructure;

import org.springframework.context.SmartLifecycle;

public abstract class AbstractSmartLifecycle implements SmartLifecycle {
    private volatile boolean running = false;

    @Override
    public void start() {
        doStart();
        running = true;
    }

    public abstract void doStart();

    @Override
    public void stop() {
        doStop();
        running = false;
    }

    public abstract void doStop();

    @Override
    public boolean isRunning() {
        return running;
    }
}