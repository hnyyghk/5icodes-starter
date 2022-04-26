package com._5icodes.starter.stress;

import com._5icodes.starter.common.utils.GrayUtils;

public interface StressConstants {
    String MODULE_NAME = "stress";
    String TRACE_TEST = "TRACE_TEST";
    String MQ_GRAY_SUFFIX = "_" + GrayUtils.getAppGroup();
    String MQ_STRESS_SUFFIX = "_" + StressConstants.TRACE_TEST;
    String DB_SUFFIX = "_" + StressConstants.TRACE_TEST.toLowerCase();
}