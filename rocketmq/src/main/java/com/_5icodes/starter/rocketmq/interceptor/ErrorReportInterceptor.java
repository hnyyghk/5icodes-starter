package com._5icodes.starter.rocketmq.interceptor;

import com._5icodes.starter.monitor.ExceptionReport;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class ErrorReportInterceptor implements MessageInterceptor {
    private final ExceptionReport exceptionReport;

    public ErrorReportInterceptor(ExceptionReport exceptionReport) {
        this.exceptionReport = exceptionReport;
    }

    @Override
    public void afterConsumeCompletion(List<MessageExt> msgs, Exception ex) {
        String topic = msgs.get(0).getTopic();
        exceptionReport.report("rocketmq:consume:" + topic, ex);
        MessageInterceptor.super.afterConsumeCompletion(msgs, ex);
    }
}