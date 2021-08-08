package com._5icodes.starter.webmvc.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.webmvc.WebMvcProperties;

public class OnAutoWrapCondition extends AbstractEnabledSpringBootCondition<WebMvcProperties> {
    public OnAutoWrapCondition() {
        super(WebConstants.PROPERTY_PREFIX, WebMvcProperties.class, WebMvcProperties::isAutoWrap);
    }
}