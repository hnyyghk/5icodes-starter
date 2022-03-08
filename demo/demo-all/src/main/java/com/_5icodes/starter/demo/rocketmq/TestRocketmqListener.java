package com._5icodes.starter.demo.rocketmq;

import com._5icodes.starter.rocketmq.annotation.RocketmqListener;
import com._5icodes.starter.rocketmq.annotation.TopicSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

@RocketmqListener(group = "TEST_GROUP", topics = {@TopicSpec(topic = "TEST_TOPIC"), @TopicSpec(topic = "TEST_TOPIC_2")})
@Slf4j
public class TestRocketmqListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : msgs) {
            log.info(new String(messageExt.getBody()));
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}