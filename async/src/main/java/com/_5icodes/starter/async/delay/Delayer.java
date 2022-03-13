package com._5icodes.starter.async.delay;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.policy.DelayTimeLevel;

public interface Delayer {
    void delay(AsyncContext context, DelayTimeLevel delayTime) throws Throwable;
}