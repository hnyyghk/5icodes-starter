package com._5icodes.starter.log;

public interface LogConstants {
    String MODULE_NAME = "log";
    String PROPERTY_PREFIX = "starter.logging";
    int DEFAULT_MAX_LENGTH = 1000;
    int DEFAULT_LINE_NUM = 10;
    int WINDOW_TIME = 1000;
    String LOG_ROLLING_METRIC_TOPIC = "T_LOG_ROLLING_METRIC_TOPIC";
    /**
     * 大日志监控
     */
    String BIG_LOG_METRIC_TOPIC = "T_BIG_LOG_METRIC_TOPIC";
    int DEFAULT_RING_BUFFER_SIZE = 128 * 1024;
}