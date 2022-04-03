package com._5icodes.starter.jdbc.monitor.condition;

import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import com._5icodes.starter.jdbc.JdbcConstants;
import com._5icodes.starter.jdbc.JdbcProperties;

public class OnDruidMonitorCondition extends AbstractEnabledSpringBootCondition<JdbcProperties> {
    public OnDruidMonitorCondition() {
        super(JdbcConstants.PROPERTY_PREFIX, JdbcProperties.class, jdbcProperties -> jdbcProperties.getMonitor().isEnable());
    }
}