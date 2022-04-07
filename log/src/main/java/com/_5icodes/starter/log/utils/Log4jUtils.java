package com._5icodes.starter.log.utils;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.LoggerConfig;

@UtilityClass
public class Log4jUtils {
    public void setLoggerLevel(String name, Level level, boolean additive) {
        org.apache.logging.log4j.core.LoggerContext loggerContext = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        LoggerConfig loggerConfig = loggerContext.getConfiguration().getLoggers().get(name);
        if (loggerConfig == null) {
            loggerConfig = new LoggerConfig(name, level, additive);
            loggerContext.getConfiguration().addLogger(name, loggerConfig);
        } else {
            loggerConfig.setLevel(level);
        }
        loggerContext.updateLoggers();
    }
}