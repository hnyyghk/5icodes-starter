package com._5icodes.starter.async.operations;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.callback.CallbackContext;
import com._5icodes.starter.async.delay.Delayer;
import com._5icodes.starter.async.policy.DelayTimeLevel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@Slf4j(topic = "com._5icodes.starter.async.operations.AsyncOperations")
public abstract class AbstractAsyncOperations implements AsyncOperations {
    private final Delayer delayer;

    protected AbstractAsyncOperations(Delayer delayer) {
        this.delayer = delayer;
    }

    @Override
    public Object runAndCheckAsync(AsyncContext context) throws Throwable {
        context.setStart(new Date());
        retryIfDelayPolicyPermit(context);
        return doRun(context);
    }

    @Override
    public Object runAndRetryAsyncIfFailed(AsyncContext context) throws Throwable {
        context.setStart(new Date());
        try {
            return doRun(context);
        } catch (Exception e) {
            //异常策略
            if (shouldRetryByExceptionRules(context, e)) {
                retryIfDelayPolicyPermit(context);
            }
            throw e;
        }
    }

    @Override
    public void runAsync(AsyncContext context) throws Throwable {
        context.setStart(new Date());
        retryIfDelayPolicyPermit(context);
    }

    @Override
    public void runInCallback(AsyncContext context) throws Throwable {
        try {
            CallbackContext.set(context);
            context.setRetryTimes(context.getRetryTimes() + 1);
            log.debug("begin to run async beanName: {} methodName: {} id: {} for {} times",
                    context.getBeanName(), context.getMethod().getName(), context.getId(), context.getRetryTimes());
            doRun(context);
            applySuccessCallback(context);
        } catch (Exception e) {
            log.warn("error happened in Asynchronous", e);
            if (shouldRetryByExceptionRules(context, e)) {
                DelayTimeLevel delayTime = getDelayTimeByPolicy(context);
                if (delayTime != null) {
                    applyFailOnceCallback(context);
                    doRetry(context, delayTime);
                } else {
                    applyFailFinalCallback(context);
                }
            } else {
                applyFailFinalCallback(context);
            }
        } finally {
            CallbackContext.remove();
        }
    }

    protected Object doRun(AsyncContext context) throws Throwable {
        try {
            return context.getMethod().invoke(context.getTarget(), context.getArguments());
        } catch (Throwable throwable) {
            Exception e;
            if (throwable instanceof InvocationTargetException) {
                e = (Exception) ((InvocationTargetException) throwable).getTargetException();
            } else if (throwable instanceof Exception) {
                e = (Exception) throwable;
            } else {
                e = new Exception(throwable);
            }
            context.setLastException(e);
            throw e;
        }
    }

    protected void retryIfDelayPolicyPermit(AsyncContext context) throws Throwable {
        DelayTimeLevel delayTime = getDelayTimeByPolicy(context);
        if (delayTime != null) {
            doRetry(context, delayTime);
        }
    }

    protected void doRetry(AsyncContext context, DelayTimeLevel delayTime) throws Throwable {
        context.setLastDelayTime(delayTime);
        delayer.delay(context, delayTime);
    }

    protected abstract DelayTimeLevel getDelayTimeByPolicy(AsyncContext context);

    protected abstract void applySuccessCallback(AsyncContext context);

    protected abstract void applyFailOnceCallback(AsyncContext context);

    protected abstract void applyFailFinalCallback(AsyncContext context);

    protected abstract boolean shouldRetryByExceptionRules(AsyncContext context, Exception e);
}