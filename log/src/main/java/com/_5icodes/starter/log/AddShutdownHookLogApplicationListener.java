package com._5icodes.starter.log;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.core.Ordered;

import java.util.concurrent.TimeUnit;

public class AddShutdownHookLogApplicationListener implements BootApplicationListener<ApplicationStartingEvent>, Ordered {
    @Override
    public void doOnApplicationEvent(ApplicationStartingEvent event) {
        org.apache.logging.log4j.core.LoggerContext loggerContext = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> loggerContext.stop(1, TimeUnit.SECONDS)));
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER + 1;
    }
}