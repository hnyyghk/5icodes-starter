package com._5icodes.starter.webmvc.auth.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnAuthCondition.class)
public @interface ConditionalOnAuth {
}