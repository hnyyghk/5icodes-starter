package com._5icodes.starter.rocketmq.consumer;

import com._5icodes.starter.rocketmq.interceptor.MessageInterceptorList;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MessageListenerOrderlyWrapper implements MessageListenerOrderly {
    private final MessageListenerOrderly delegate;
    private final MessageInterceptorList interceptorList;

    public MessageListenerOrderlyWrapper(MessageListenerOrderly delegate, MessageInterceptorList interceptorList) {
        this.delegate = delegate;
        this.interceptorList = interceptorList;
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        return interceptorList.consumeMessage(
                msgs,
                list -> delegate.consumeMessage(list, context),
                ConsumeOrderlyStatus.SUCCESS);
    }
}