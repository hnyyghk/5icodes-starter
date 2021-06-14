package com._5icodes.starter.stress.feign.test.remote;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import com._5icodes.starter.stress.feign.test.local.MockClient;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MockServerStartListener implements BootApplicationListener<ApplicationStartedEvent> {
    /**
     * 定时线程
     */
    private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("mock-task-%d").daemon(true).build());
    /**
     * mockProperties
     */
    private final MockProperties mockProperties;

    public MockServerStartListener(MockProperties mockProperties) {
        this.mockProperties = mockProperties;
    }

    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        if (mockProperties.getEnable()) {
            EXECUTOR.scheduleAtFixedRate(MockClient::pull, 0, 60, TimeUnit.SECONDS);
        }
    }
}