package com._5icodes.starter.sentinel.log;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.log.LogTarget;
import com.alibaba.csp.sentinel.log.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = RecordLog.LOGGER_NAME)
@LogTarget(RecordLog.LOGGER_NAME)
public class RecordLogLoggerImpl implements Logger {
    @Override
    public void info(String format, Object... arguments) {
        log.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable e) {
        log.info(msg, e);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log.warn(format, arguments);
    }

    @Override
    public void warn(String msg, Throwable e) {
        log.warn(msg, e);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable e) {
        log.trace(msg, e);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable e) {
        log.debug(msg, e);
    }

    @Override
    public void error(String format, Object... arguments) {
        log.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable e) {
        log.error(msg, e);
    }
}