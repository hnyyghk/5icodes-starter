package com._5icodes.starter.log;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class LogPropertiesProcessApplicationListener implements BootApplicationListener<ApplicationStartedEvent> {
    private final LogProperties logProperties;

    public LogPropertiesProcessApplicationListener(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        org.apache.logging.log4j.core.LoggerContext loggerContext = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        String[] profiles = context.getEnvironment().getActiveProfiles();
        if (ArrayUtils.contains(profiles, AbstractProfileEnvironmentPostProcessor.LOCAL) || logProperties.isDocker()) {
            return;
        }
        Logger rootLogger = loggerContext.getRootLogger();
        Appender console = rootLogger.getAppenders().get("Console");
        if (console != null) {
            rootLogger.removeAppender(console);
        }
    }
}