package com._5icodes.starter.rocketmq;

import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.stress.StressConstants;

public interface RocketmqConstants {
    String MODULE_NAME = "rocketmq";
    String MQ_GRAY_SUFFIX = "_" + GrayUtils.getAppGroup();
    String MQ_STRESS_SUFFIX = "_" + StressConstants.TRACE_TEST;
    String PROPERTY_PREFIX = "starter.rocketmq";
}