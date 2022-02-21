package com._5icodes.starter.rocketmq.consumer;

import com._5icodes.starter.rocketmq.interceptor.MessageInterceptorList;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MessageListenerConcurrentlyWrapper implements MessageListenerConcurrently {
    private final MessageListenerConcurrently delegate;
    private final MessageInterceptorList interceptorList;

    public MessageListenerConcurrentlyWrapper(MessageListenerConcurrently delegate, MessageInterceptorList interceptorList) {
        this.delegate = delegate;
        this.interceptorList = interceptorList;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        return interceptorList.consumeMessage(
                msgs,
                list -> delegate.consumeMessage(list, context),
                ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
    }
}