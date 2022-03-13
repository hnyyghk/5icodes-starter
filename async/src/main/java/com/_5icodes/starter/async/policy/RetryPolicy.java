package com._5icodes.starter.async.policy;

import com._5icodes.starter.async.AsyncContext;

public interface RetryPolicy {
    DelayTimeLevel getNextRetryDelayTime(AsyncContext context);
}