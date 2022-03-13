package com._5icodes.starter.demo.async;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.callback.AsyncCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestCallback implements AsyncCallback {
    @Override
    public void success(AsyncContext context) {
        logContext(context, "success");
    }

    @Override
    public void failOnce(AsyncContext context) {
        logContext(context, "failOnce");
    }

    @Override
    public void failFinal(AsyncContext context) {
        logContext(context, "failFinal");
    }

    private void logContext(AsyncContext context, String s) {
        Exception lastException = context.getLastException();
        log.info("context lastDelayTime: {}, lastException: {}, retryTimes: {}, start:{}",
                context.getLastDelayTime(),
                lastException == null ? "null" : lastException.getMessage(),
                context.getRetryTimes(),
                context.getStart());
        log.info("{} arguments: {}", s, context.getArguments());
    }
}