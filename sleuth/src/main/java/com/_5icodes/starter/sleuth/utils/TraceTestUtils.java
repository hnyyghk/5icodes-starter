package com._5icodes.starter.sleuth.utils;

import com._5icodes.starter.sleuth.SleuthConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@UtilityClass
public class TraceTestUtils {
    public boolean isTraceTest() {
        return null != MDC.get(SleuthConstants.TRACE_TEST);
    }

    public void info(String format, Object... arguments) {
        if (log.isDebugEnabled()) {
            log.info(format, arguments);
        }
    }

    public void info(boolean enable, String format, Object... arguments) {
        if (log.isDebugEnabled() && enable) {
            log.info(format, arguments);
        }
    }
}