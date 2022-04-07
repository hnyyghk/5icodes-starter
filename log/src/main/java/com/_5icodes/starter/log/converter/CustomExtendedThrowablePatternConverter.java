package com._5icodes.starter.log.converter;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.log.LogConstants;
import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;
import org.apache.logging.log4j.spi.StandardLevel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @see org.apache.logging.log4j.core.pattern.ExtendedThrowablePatternConverter
 */
@Slf4j
@Plugin(name = "CustomExtendedThrowablePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys("customEx")
public class CustomExtendedThrowablePatternConverter extends ThrowablePatternConverter {
    private static final String CAUSED_BY_LABEL = "Caused by: ";
    private static final String EMPTY_BY_LABEL = "";
    private static volatile CustomExtendedThrowablePatternConverter instance;
    @Setter
    private int lineNum;
    @Setter
    private long preTimeMillis;
    private final int[] collectors = new int[]{0, 0, 0, 0, 0};

    private CustomExtendedThrowablePatternConverter(Configuration config, String[] options, int lineNum) {
        super("CustomExtendedThrowable", "throwable", options, config);
        this.lineNum = lineNum;
    }

    public static CustomExtendedThrowablePatternConverter getInstance() {
        return instance;
    }

    @Override
    public void format(LogEvent event, StringBuilder buffer) {
        report(event);
        Throwable throwable = event.getThrown();
        ThrowableProxy proxy = event.getThrownProxy();
        if (throwable == null && proxy == null) {
            return;
        }
        if (proxy == null) {
            super.format(event, buffer);
            return;
        }
        int len = buffer.length();
        if (len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
            buffer.append(' ');
        }
        if (lineNum <= 0) {
            proxy.formatExtendedStackTraceTo(buffer, options.getIgnorePackages(),
                    options.getTextRenderer(), getSuffix(event), options.getSeparator());
        } else {
            appendMessage(buffer, proxy);
            appendStackTrace(buffer, proxy, lineNum);
            appendCause(buffer, proxy);
        }
    }

    private void appendCause(StringBuilder buffer, ThrowableProxy proxy) {
        ThrowableProxy causeProxy = proxy.getCauseProxy();
        if (causeProxy == null) {
            return;
        }
        while (causeProxy.getCauseProxy() != null) {
            buffer.append(CAUSED_BY_LABEL);
            appendMessage(buffer, causeProxy);
            causeProxy = causeProxy.getCauseProxy();
        }
        buffer.append(CAUSED_BY_LABEL);
        appendMessage(buffer, causeProxy);
        appendStackTrace(buffer, causeProxy, lineNum);
        int commonElementCount = causeProxy.getCommonElementCount();
        if (commonElementCount != 0) {
            buffer.append("\t... ").append(commonElementCount).append(" more").append(options.getSeparator());
        }
    }

    private void appendStackTrace(StringBuilder buffer, ThrowableProxy proxy, int lineNum) {
        StackTraceElement[] stackTrace = proxy.getStackTrace();
        if (stackTrace == null || stackTrace.length == 0) {
            return;
        }
        int length = stackTrace.length;
        int i = 0;
        while (lineNum-- > 0 && i < length) {
            buffer.append("\tat ");
            buffer.append(stackTrace[i++]).append(options.getSeparator());
        }
    }

    private void appendMessage(StringBuilder buffer, ThrowableProxy proxy) {
        Throwable throwable = proxy.getThrowable();
        buffer.append(throwable.getClass().getName())
                .append(": ")
                .append(throwable.getLocalizedMessage() == null ? EMPTY_BY_LABEL : throwable.getLocalizedMessage())
                .append(options.getSeparator());
    }

    private void report(LogEvent event) {
        if (preTimeMillis <= 0) {
            return;
        }
        long eventTimeMillis = event.getTimeMillis();
        if (eventTimeMillis - preTimeMillis > LogConstants.WINDOW_TIME) {
            Map<String, Object> map = new HashMap<>();
            map.put(StandardLevel.ERROR.name().toLowerCase(), collectors[0]);
            map.put(StandardLevel.WARN.name().toLowerCase(), collectors[1]);
            map.put(StandardLevel.INFO.name().toLowerCase(), collectors[2]);
            map.put(StandardLevel.DEBUG.name().toLowerCase(), collectors[3]);
            map.put(StandardLevel.TRACE.name().toLowerCase(), collectors[4]);
            map.put("reportTime", preTimeMillis);
            map.put("app", SpringApplicationUtils.getApplicationName());
            map.put("zone", RegionUtils.getZone());
            MonitorKafkaTemplate.getInstance().send(LogConstants.LOG_ROLLING_METRIC_TOPIC, JsonUtils.toJson(map));
            preTimeMillis = eventTimeMillis;
            Arrays.fill(collectors, 0);
        }
        switch (event.getLevel().getStandardLevel()) {
            case ERROR:
                collectors[0]++;
                break;
            case WARN:
                collectors[1]++;
                break;
            case INFO:
                collectors[2]++;
                break;
            case DEBUG:
                collectors[3]++;
                break;
            case TRACE:
                collectors[4]++;
                break;
            default:
                break;
        }
    }

    public static CustomExtendedThrowablePatternConverter newInstance(Configuration config, String[] options) {
        if (instance != null) {
            return instance;
        }
        if (options.length < 1) {
            LOGGER.error("Incorrect number of options on maxLength: expected at least 1 received {}: {}", options.length,
                    options);
            return null;
        }
        String lineNumStr = options[options.length - 1];
        if (lineNumStr == null) {
            LOGGER.error("No lineNum supplied on maxLength");
            return null;
        }
        instance = new CustomExtendedThrowablePatternConverter(config, ArrayUtils.subarray(options, 0, options.length - 1),
                AbstractAppender.parseInt(lineNumStr, LogConstants.DEFAULT_LINE_NUM));
        return instance;
    }
}