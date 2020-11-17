package com._5icodes.starter.web.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnAutoWrapCondition.class)
public @interface ConditionalOnAutoWrap {
}