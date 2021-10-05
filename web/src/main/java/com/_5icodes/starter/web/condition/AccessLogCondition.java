package com._5icodes.starter.web.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.web.WebProperties;

public class AccessLogCondition extends AbstractEnabledSpringBootCondition<WebProperties> {
    public AccessLogCondition() {
        super(WebConstants.COMMON_PREFIX, WebProperties.class, WebProperties::isAccessLogEnabled);
    }
}