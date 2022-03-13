package com._5icodes.starter.monitor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorDataWrapperExceptionHandler implements ExceptionHandler<MonitorDataWrapper> {
    @Override
    public void handleEventException(Throwable ex, long sequence, MonitorDataWrapper event) {
        log.error("process disruptor event failed", ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        log.error("start disruptor failed", ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        log.error("shutdown disruptor failed", ex);
    }
}