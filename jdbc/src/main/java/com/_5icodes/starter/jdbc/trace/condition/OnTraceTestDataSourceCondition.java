package com._5icodes.starter.jdbc.trace.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.jdbc.JdbcConstants;
import com._5icodes.starter.jdbc.JdbcProperties;

public class OnTraceTestDataSourceCondition extends AbstractEnabledSpringBootCondition<JdbcProperties> {
    public OnTraceTestDataSourceCondition() {
        super(JdbcConstants.PROPERTY_PREFIX, JdbcProperties.class, JdbcProperties::isTraceTestEnable);
    }
}