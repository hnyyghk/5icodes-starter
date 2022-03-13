package com._5icodes.starter.async.consumer;

import com._5icodes.starter.rocketmq.annotation.RocketmqListener;
import com._5icodes.starter.rocketmq.annotation.TopicSpec;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

@RocketmqListener(group = "",
        topics = {@TopicSpec(topic = "")})
public class AsyncMessageListenerOrderly implements MessageListenerOrderly {
    private final AsyncRocketmqMessageListener asyncRocketmqMessageListener;

    public AsyncMessageListenerOrderly(AsyncRocketmqMessageListener asyncRocketmqMessageListener) {
        this.asyncRocketmqMessageListener = asyncRocketmqMessageListener;
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        return asyncRocketmqMessageListener.consumeMessage(msgs) ?
                ConsumeOrderlyStatus.SUCCESS :
                ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
    }
}