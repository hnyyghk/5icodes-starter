package com._5icodes.starter.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = LogConstants.PROPERTY_PREFIX)
public class LogProperties {
    private boolean showSql = false;

    private String logDir;

    private boolean bigLogEnable = Boolean.FALSE;

    private int maxLength = LogConstants.DEFAULT_MAX_LENGTH;

    private int lineNum = LogConstants.DEFAULT_LINE_NUM;

    private String consoleLogPattern = "%clr{%d{${LOG_DATEFORMAT_PATTERN}}}{faint} %clr{${LOG_LEVEL_PATTERN}} %X{userId} %X{traceId} %X{spanId} %X{zone} %X{gray} %clr{---}{faint} %clr{[%15.15t]}{faint} %clr{%-40.40c{1.}}{cyan} %clr{:}{faint} %customMaxLen{%m}{${sys:LOG_MAX_LEN}}%n%customEx{${sys:LOG_LINE_NUM}}";

    private String fileLogPattern = "%d{${LOG_DATEFORMAT_PATTERN}} ${LOG_LEVEL_PATTERN} %X{userId} %X{traceId} %X{spanId} %X{zone} %X{gray} %.15t %c{1.} %customMaxLen{%m}{${sys:LOG_MAX_LEN}}%n%customEx{${sys:LOG_LINE_NUM}}";

    private boolean docker = false;

    private boolean enableTraceTestFile = false;

    private int ringBufferSize = LogConstants.DEFAULT_RING_BUFFER_SIZE;
}