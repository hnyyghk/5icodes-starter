package com._5icodes.starter.rocketmq.trace;

import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.rocketmq.RocketmqConstants;
import com._5icodes.starter.rocketmq.RocketmqProperties;
import com._5icodes.starter.rocketmq.interceptor.MessageInterceptor;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class TraceTestMessageInterceptor implements MessageInterceptor {
    private final RocketmqProperties properties;

    public TraceTestMessageInterceptor(RocketmqProperties properties) {
        this.properties = properties;
    }

    @Override
    public Collection<Message> preSend(Collection<Message> messages) {
        List<String> grayTopics = properties.getGrayTopics();
        for (Message message : messages) {
            String topic = message.getTopic();
            if (GrayUtils.isAppGroup() && !CollectionUtils.isEmpty(grayTopics) && grayTopics.contains(topic)) {
                message.setTopic(topic + RocketmqConstants.MQ_GRAY_SUFFIX);
            } else if (TraceTestUtils.isTraceTest()) {
                message.setTopic(topic + RocketmqConstants.MQ_STRESS_SUFFIX);
                TraceTestUtils.info("this is trace test rocketmq producer topic: {}", message.getTopic());
            }
        }
        return MessageInterceptor.super.preSend(messages);
    }

    @Override
    public List<MessageExt> preConsume(List<MessageExt> msgs) {
        MessageExt message = msgs.get(0);
        String topic = message.getTopic();
        if (topic.endsWith(RocketmqConstants.MQ_STRESS_SUFFIX)) {
            TraceTestUtils.info("this is trace test rocketmq consumer topic: {}", message.getTopic());
            TraceTestUtils.setTraceTestFlag();
        }
        return MessageInterceptor.super.preConsume(msgs);
    }
}