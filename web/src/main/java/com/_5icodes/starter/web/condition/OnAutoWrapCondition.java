package com._5icodes.starter.web.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.web.WebProperties;

public class OnAutoWrapCondition extends AbstractEnabledSpringBootCondition<WebProperties> {
    public OnAutoWrapCondition() {
        super(WebConstants.PROPERTY_PREFIX, WebProperties.class, WebProperties::isAutoWrap);
    }
}