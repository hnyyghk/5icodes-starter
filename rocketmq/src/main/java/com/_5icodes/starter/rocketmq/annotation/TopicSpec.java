package com._5icodes.starter.rocketmq.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TopicSpec {
    String topic();

    String tags() default "";

    String sql() default "";
}