package com._5icodes.starter.redisson.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Locked {
    String key() default "";

    long waitTime() default RedisLockUtils.DEFAULT_WAIT_TIME;

    long leaseTime() default RedisLockUtils.DEFAULT_LEASE_TIME;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String message() default RedisLockUtils.DEFAULT_MESSAGE;

    boolean catchException() default true;
}