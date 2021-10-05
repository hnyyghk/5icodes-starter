package com._5icodes.starter.web.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.web.WebProperties;

public class InternalServerCondition extends AbstractEnabledSpringBootCondition<WebProperties> {
    public InternalServerCondition() {
        super(WebConstants.COMMON_PREFIX, WebProperties.class, WebProperties::isInternalEnabled);
    }
}