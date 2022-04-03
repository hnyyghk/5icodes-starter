package com._5icodes.starter.jdbc.monitor.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnDruidMonitorCondition.class)
public @interface ConditionalOnDruidMonitor {
}