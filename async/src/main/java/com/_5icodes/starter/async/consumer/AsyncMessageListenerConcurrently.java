package com._5icodes.starter.async.consumer;

import com._5icodes.starter.rocketmq.annotation.RocketmqListener;
import com._5icodes.starter.rocketmq.annotation.TopicSpec;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

@RocketmqListener(group = "",
        topics = {@TopicSpec(topic = "")})
public class AsyncMessageListenerConcurrently implements MessageListenerConcurrently {
    private final AsyncRocketmqMessageListener asyncRocketmqMessageListener;

    public AsyncMessageListenerConcurrently(AsyncRocketmqMessageListener asyncRocketmqMessageListener) {
        this.asyncRocketmqMessageListener = asyncRocketmqMessageListener;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        return asyncRocketmqMessageListener.consumeMessage(msgs) ?
                ConsumeConcurrentlyStatus.CONSUME_SUCCESS :
                ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}