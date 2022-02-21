package com._5icodes.starter.rocketmq.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class MessageInterceptorList {
    private final List<MessageInterceptor> interceptors;

    public MessageInterceptorList(List<MessageInterceptor> interceptors) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            AnnotationAwareOrderComparator.sort(interceptors);
        }
        this.interceptors = interceptors;
    }

    public <S> S consumeMessage(List<MessageExt> msgs, Function<List<MessageExt>, S> function, S defaultStatus) {
        Deque<MessageInterceptor> interceptorDeque = null;
        try {
            if (!CollectionUtils.isEmpty(interceptors)) {
                interceptorDeque = new ArrayDeque<>();
                msgs = preConsume(msgs, interceptorDeque);
                if (CollectionUtils.isEmpty(msgs)) {
                    return defaultStatus;
                }
            }
            S result = function.apply(msgs);
            if (interceptorDeque != null) {
                afterConsumeCompletion(msgs, null, interceptorDeque);
            }
            return result;
        } catch (Exception ex) {
            if (interceptorDeque != null) {
                afterConsumeCompletion(msgs, ex, interceptorDeque);
            }
            throw ex;
        }
    }

    private List<MessageExt> preConsume(List<MessageExt> msgs, Deque<MessageInterceptor> interceptorDeque) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            List<MessageExt> previous = msgs;
            for (MessageInterceptor interceptor : interceptors) {
                msgs = interceptor.preConsume(previous);
                if (CollectionUtils.isEmpty(msgs)) {
                    afterConsumeCompletion(previous, null, interceptorDeque);
                    return Collections.emptyList();
                }
                interceptorDeque.add(interceptor);
                previous = msgs;
            }
        }
        return msgs;
    }

    private void afterConsumeCompletion(List<MessageExt> msgs, Exception ex, Deque<MessageInterceptor> interceptorDeque) {
        for (MessageInterceptor interceptor : interceptorDeque) {
            try {
                interceptor.afterConsumeCompletion(msgs, ex);
            } catch (Exception e) {
                log.error("Exception from afterConsumeCompletion in {}", interceptor, e);
            }
        }
    }

    public Object send(Collection<Message> messages, ProceedingJoinPoint pjp) throws Throwable {
        Deque<MessageInterceptor> interceptorDeque = null;
        try {
            if (!CollectionUtils.isEmpty(interceptors)) {
                interceptorDeque = new ArrayDeque<>();
                messages = preSend(messages, interceptorDeque);
                if (CollectionUtils.isEmpty(messages)) {
                    return null;
                }
            }
            Object result = pjp.proceed();
            SendResult sendResult = result instanceof SendResult ? (SendResult) result : null;
            if (interceptorDeque != null) {
                postSend(messages, sendResult);
                afterSendCompletion(messages, sendResult, null, interceptorDeque);
            }
            return result;
        } catch (Exception ex) {
            if (interceptorDeque != null) {
                afterSendCompletion(messages, null, ex, interceptorDeque);
            }
            throw ex;
        }
    }

    private Collection<Message> preSend(Collection<Message> messages, Deque<MessageInterceptor> interceptorDeque) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            Collection<Message> previous = messages;
            for (MessageInterceptor interceptor : interceptors) {
                messages = interceptor.preSend(previous);
                if (CollectionUtils.isEmpty(messages)) {
                    afterSendCompletion(previous, null, null, interceptorDeque);
                    return null;
                }
                interceptorDeque.add(interceptor);
                previous = messages;
            }
        }
        return messages;
    }

    private void postSend(Collection<Message> messages, SendResult sendResult) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            for (MessageInterceptor interceptor : interceptors) {
                interceptor.postSend(messages, sendResult);
            }
        }
    }

    private void afterSendCompletion(Collection<Message> messages, SendResult sendResult, Exception ex, Deque<MessageInterceptor> interceptorDeque) {
        for (MessageInterceptor interceptor : interceptorDeque) {
            try {
                interceptor.afterSendCompletion(messages, sendResult, ex);
            } catch (Exception e) {
                log.error("Exception from afterSendCompletion in {}", interceptor, e);
            }
        }
    }
}