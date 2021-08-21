package com._5icodes.starter.sentinel.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnSentinelCondition.class)
public @interface ConditionalOnSentinel {
}