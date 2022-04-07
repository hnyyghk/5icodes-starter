package com._5icodes.starter.log.converter;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.log.LogConstants;
import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.*;
import org.apache.logging.log4j.util.PerformanceSensitive;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see org.apache.logging.log4j.core.pattern.MaxLengthConverter
 */
@Plugin(name = "CustomMaxLength", category = PatternConverter.CATEGORY)
@ConverterKeys("customMaxLen")
@PerformanceSensitive("allocation")
@Slf4j
public class CustomDynamicMaxLengthConverter extends LogEventPatternConverter {
    private static final int DOT_LEN = 20;
    private static volatile CustomDynamicMaxLengthConverter instance;
    private final List<PatternFormatter> formatters;
    private static long startTime;
    private static Map<String, Map<String, Object>> cache = new HashMap<>(50);

    @Setter
    private int maxLength;
    @Setter
    private boolean bigLogEnable;

    private CustomDynamicMaxLengthConverter(List<PatternFormatter> formatters, int maxLength) {
        super("CustomMaxLength", "customMaxLength");
        this.formatters = formatters;
        this.maxLength = maxLength;
    }

    public static CustomDynamicMaxLengthConverter getInstance() {
        return instance;
    }

    public static void setStartTime(long currentTimeMillis) {
        startTime = currentTimeMillis;
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        int initialLength = toAppendTo.length();
        for (PatternFormatter formatter : formatters) {
            formatter.format(event, toAppendTo);
            if (toAppendTo.length() > initialLength + maxLength) {
                break;
            }
        }
        int kbSize = toAppendTo.length() / 1024;
        uploadBigLog(kbSize, event);
        if (toAppendTo.length() > initialLength + maxLength) {
            toAppendTo.setLength(initialLength + maxLength);
            if (maxLength > DOT_LEN) {
                toAppendTo.append("... more than ").append(kbSize).append("kb");
            }
        }
    }

    private void uploadBigLog(int kbSize, LogEvent event) {
        if (startTime <= 0) {
            return;
        }
        if (bigLogEnable) {
            cache.clear();
            return;
        }
        countByLoggerObject(kbSize, event);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - startTime > LogConstants.WINDOW_TIME) {
            Collection<Map<String, Object>> list = cache.values();
            if (!list.isEmpty()) {
                Map<String, Object> message = new HashMap<>();
                message.put("app", SpringApplicationUtils.getApplicationName());
                message.put("reportTime", currentTimeMillis);
                message.put("zone", RegionUtils.getZone());
                message.put("reportResult", list);
                MonitorKafkaTemplate.getInstance().send(LogConstants.BIG_LOG_METRIC_TOPIC, JsonUtils.toJson(message));
                startTime = currentTimeMillis;
                cache = new HashMap<>(50);
            }
        }
    }

    /**
     * 按照logger对象统计次数和大小
     *
     * @param kbSize
     * @param event
     */
    private void countByLoggerObject(int kbSize, LogEvent event) {
        if (kbSize <= 0) {
            return;
        }
        String loggerName = event.getLoggerName();
        Map<String, Object> map = cache.get(loggerName);
        if (map == null) {
            map = new HashMap<>();
            map.put("loggerName", loggerName);
            map.put("times", 1);
            map.put("size", kbSize);
        } else {
            int times = (Integer) map.get("times") + 1;
            int size = (Integer) map.get("size") + kbSize;
            map.put("loggerName", loggerName);
            map.put("times", times);
            map.put("size", size);
        }
        cache.put(loggerName, map);
    }

    public static CustomDynamicMaxLengthConverter newInstance(Configuration config, String[] options) {
        if (instance != null) {
            return instance;
        }
        if (options.length != 2) {
            LOGGER.error("Incorrect number of options on maxLength: expected 2 received {}: {}", options.length,
                    options);
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on maxLength");
            return null;
        }
        if (options[1] == null) {
            LOGGER.error("No length supplied on maxLength");
            return null;
        }
        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        instance = new CustomDynamicMaxLengthConverter(formatters, AbstractAppender.parseInt(options[1], LogConstants.DEFAULT_MAX_LENGTH));
        return instance;
    }
}