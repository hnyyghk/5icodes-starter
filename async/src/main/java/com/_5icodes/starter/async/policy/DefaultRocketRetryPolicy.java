package com._5icodes.starter.async.policy;

import com._5icodes.starter.async.AsyncContext;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DefaultRocketRetryPolicy implements RetryPolicy {
    private DelayTimeLevel firstDelayTime;
    private int step = 1;
    private int maxRetry = Integer.MAX_VALUE;
    private long expireTime = -1;

    public void setFirstDelayTime(DelayTimeLevel firstDelayTime) {
        this.firstDelayTime = firstDelayTime;
    }

    public void setFirstDelayLevel(int firstDelayLevel) {
        try {
            this.firstDelayTime = DelayTimeLevel.values()[firstDelayLevel];
        } catch (Exception e) {
            throw new IllegalArgumentException("firstDelayLevel is invalid", e);
        }
    }

    public void setStep(int step) {
        if (step >= 0) {
            this.step = step;
        }
    }

    public void setMaxRetry(int maxRetry) {
        if (maxRetry > 0) {
            this.maxRetry = maxRetry;
        }
    }

    public void setTimeGapOfTimeUnit(TimeUnit expireTimeUnit, long expireTime) {
        if (expireTime > 0) {
            this.expireTime = expireTimeUnit.toMillis(expireTime);
        }
    }

    public void setExpireTime(long expireTime) {
        if (expireTime >= 0) {
            this.expireTime = expireTime;
        }
    }

    @Override
    public DelayTimeLevel getNextRetryDelayTime(AsyncContext context) {
        if (context == null) {
            return firstDelayTime;
        }
        if (context.getLastDelayTime() == null || context.getRetryTimes() == 0) {
            return firstDelayTime;
        }
        if (context.getRetryTimes() >= maxRetry) {
            return null;
        }
        if (expireTime > 0) {
            Date start = context.getStart();
            if (System.currentTimeMillis() - start.getTime() > expireTime) {
                return null;
            }
        }
        DelayTimeLevel[] values = DelayTimeLevel.values();
        DelayTimeLevel lastDelayTime = context.getLastDelayTime();
        if (step == 0) {
            return lastDelayTime;
        }
        int nextStep = lastDelayTime.ordinal() + step;
        if (nextStep >= values.length) {
            nextStep = values.length - 1;
        }
        return values[nextStep];
    }
}