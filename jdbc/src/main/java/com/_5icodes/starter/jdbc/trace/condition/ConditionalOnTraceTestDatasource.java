package com._5icodes.starter.jdbc.trace.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnTraceTestDatasourceCondition.class)
public @interface ConditionalOnTraceTestDatasource {
}