package com._5icodes.starter.async.configuration;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnConcurrentlyCondition.class)
public @interface ConditionalOnConcurrently {
}