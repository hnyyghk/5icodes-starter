package com._5icodes.starter.rocketmq.sleuth;

import brave.Span;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalSpan;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com._5icodes.starter.rocketmq.interceptor.MessageInterceptor;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.core.Ordered;

import java.util.Collection;
import java.util.List;

public class TraceMessageInterceptor implements MessageInterceptor, Ordered {
    private final ThreadLocalSpan threadLocalSpan;
    private final TraceContext.Injector<Message> injector;
    private final TraceContext.Extractor<MessageExt> extractor;

    public TraceMessageInterceptor(Tracing tracing, Propagation.Setter<Message, String> setter, Propagation.Getter<MessageExt, String> getter) {
        this.threadLocalSpan = ThreadLocalSpan.create(tracing.tracer());
        this.injector = tracing.propagation().injector(setter);
        this.extractor = tracing.propagation().extractor(getter);
    }

    @Override
    public List<MessageExt> preConsume(List<MessageExt> msgs) {
        if (msgs.size() == 1) {
            MessageExt messageExt = msgs.get(0);
            TraceContextOrSamplingFlags extracted = extractor.extract(messageExt);
            threadLocalSpan.next(extracted);
        } else {
            this.threadLocalSpan.next();
        }
        return MessageInterceptor.super.preConsume(msgs);
    }

    @Override
    public void afterConsumeCompletion(List<MessageExt> msgs, Exception ex) {
        finishSpan(ex);
        MessageInterceptor.super.afterConsumeCompletion(msgs, ex);
    }

    @Override
    public Collection<Message> preSend(Collection<Message> messages) {
        Span span = threadLocalSpan.next();
        for (Message message : messages) {
            injector.inject(span.context(), message);
        }
        return MessageInterceptor.super.preSend(messages);
    }

    @Override
    public void afterSendCompletion(Collection<Message> messages, SendResult sendResult, Exception ex) {
        finishSpan(ex);
        MessageInterceptor.super.afterSendCompletion(messages, sendResult, ex);
    }

    private void finishSpan(Exception ex) {
        Span span = threadLocalSpan.remove();
        if (span == null || span.isNoop()) {
            return;
        }
        if (ex != null) {
            String message = ex.getMessage();
            if (message == null) {
                message = ex.getClass().getSimpleName();
            }
            span.tag("error", message);
        }
        span.finish();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}