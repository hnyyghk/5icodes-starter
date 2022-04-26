package com._5icodes.starter.stress.utils;

import com._5icodes.starter.sleuth.SleuthConstants;
import com._5icodes.starter.sleuth.utils.BaggageFieldUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TraceTestUtils {
    public boolean isTraceTest() {
        return null != BaggageFieldUtils.get(SleuthConstants.TRACE_TEST_HEADER);
    }

    public void info(String format, Object... arguments) {
        if (log.isDebugEnabled()) {
            log.info(format, arguments);
        }
    }
}