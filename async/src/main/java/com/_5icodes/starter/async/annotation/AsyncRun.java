package com._5icodes.starter.async.annotation;

import com._5icodes.starter.async.callback.AsyncCallback;
import com._5icodes.starter.async.policy.DelayTimeLevel;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncRun {
    /**
     * 默认使用uuid，可以使用spel表达式
     */
    String id() default "";

    /**
     * 异步模式
     */
    Mode mode() default Mode.ASYNC;

    /**
     * 是否需要保证顺序消费
     */
    boolean orderly() default false;

    /**
     * 第一次的延迟时间
     */
    DelayTimeLevel firstDelayTime() default DelayTimeLevel.NO_DELAY;

    /**
     * 每次失败，下次执行的延迟时间的增加量，DelayTimeLevel的ordinal
     */
    int step() default 1;

    /**
     * 最大重试次数
     */
    int maxRetry() default 1;

    /**
     * 数据有效期
     */
    long expireTime() default -1;

    /**
     * 数据有效期的单位
     */
    TimeUnit expireTimeUnit() default TimeUnit.HOURS;

    /**
     * 回调函数的spring beanName
     */
    String callbackName() default "";

    /**
     * 回调函数的class
     */
    Class<? extends AsyncCallback> callbackClass() default AsyncCallback.class;

    /**
     * 需要重试的异常类
     */
    Class<? extends Exception>[] retryFor() default {};

    /**
     * 不需要重试的异常类
     */
    Class<? extends Exception>[] notRetryFor() default {};
}