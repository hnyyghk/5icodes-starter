package com._5icodes.starter.webmvc.auth.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.webmvc.WebMvcConstants;
import com._5icodes.starter.webmvc.WebMvcProperties;

public class OnAuthCondition extends AbstractEnabledSpringBootCondition<WebMvcProperties> {
    public OnAuthCondition() {
        super(WebMvcConstants.PROPERTY_PREFIX, WebMvcProperties.class, WebMvcProperties::isAuthEnabled);
    }
}