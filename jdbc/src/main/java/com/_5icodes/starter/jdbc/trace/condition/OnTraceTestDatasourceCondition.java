package com._5icodes.starter.jdbc.trace.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.jdbc.JdbcConstants;
import com._5icodes.starter.jdbc.JdbcProperties;

public class OnTraceTestDatasourceCondition extends AbstractEnabledSpringBootCondition<JdbcProperties> {
    public OnTraceTestDatasourceCondition() {
        super(JdbcConstants.PROPERTY_PREFIX, JdbcProperties.class, JdbcProperties::isTraceTestEnable);
    }
}