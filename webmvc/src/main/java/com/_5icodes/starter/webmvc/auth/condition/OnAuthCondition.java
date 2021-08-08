package com._5icodes.starter.webmvc.auth.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.webmvc.WebMvcProperties;

public class OnAuthCondition extends AbstractEnabledSpringBootCondition<WebMvcProperties> {
    public OnAuthCondition() {
        super(WebConstants.PROPERTY_PREFIX, WebMvcProperties.class, WebMvcProperties::isAuthEnabled);
    }
}