package com._5icodes.starter.sentinel.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.sentinel.SentinelConstants;
import com._5icodes.starter.sentinel.SentinelProperties;

public class OnSentinelCondition extends AbstractEnabledSpringBootCondition<SentinelProperties> {
    public OnSentinelCondition() {
        super(SentinelConstants.PROPERTY_PREFIX, SentinelProperties.class, SentinelProperties::isEnabled);
    }
}