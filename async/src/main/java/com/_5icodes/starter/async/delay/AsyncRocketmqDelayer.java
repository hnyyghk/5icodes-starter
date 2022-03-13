package com._5icodes.starter.async.delay;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.AsyncProperties;
import com._5icodes.starter.async.codec.AsyncCodec;
import com._5icodes.starter.async.policy.DelayTimeLevel;
import com._5icodes.starter.rocketmq.RocketmqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class AsyncRocketmqDelayer implements Delayer {
    private final AsyncProperties asyncProperties;
    private final DefaultMQProducer defaultMqProducer;
    private final AsyncCodec asyncCodec;

    public AsyncRocketmqDelayer(AsyncProperties asyncProperties, DefaultMQProducer defaultMqProducer, AsyncCodec asyncCodec) {
        this.asyncProperties = asyncProperties;
        this.defaultMqProducer = defaultMqProducer;
        this.asyncCodec = asyncCodec;
    }

    @Override
    public void delay(AsyncContext context, DelayTimeLevel delayTime) throws Throwable {
        Method method = context.getMethod();
        Assert.notNull(method, "method must not be null");
        Message message = new Message();
        RocketmqProperties.TopicSpec topicSpec;
        if (context.getOrderly()) {
            topicSpec = asyncProperties.getOrder().getTopics().get(0);
        } else {
            topicSpec = asyncProperties.getTopics().get(0);
        }
        message.setTopic(topicSpec.getTopic());
        String tags = topicSpec.getTags();
        if (StringUtils.hasText(tags)) {
            message.setTags(tags);
        }
        if (delayTime != null && delayTime != DelayTimeLevel.NO_DELAY) {
            message.setDelayTimeLevel(delayTime.ordinal());
        }
        String id = context.getId();
        if (StringUtils.hasText(id)) {
            message.setKeys(id);
        }
        AsyncSerializableObj obj = new AsyncSerializableObj();
        obj.setContext(context);
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMethodName(method.getName());
        methodInfo.setParameterTypes(method.getParameterTypes());
        methodInfo.setDeclaringClass(method.getDeclaringClass());
        obj.setMethodInfo(methodInfo);
        message.setBody(asyncCodec.encode(obj));
        if (id != null) {
            defaultMqProducer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    String s = (String) arg;
                    return mqs.get(Math.abs(s.hashCode()) % mqs.size());
                }
            }, id);
        } else {
            defaultMqProducer.send(message);
        }
        log.debug("send msg key: {} tag: {}", message.getKeys(), message.getTags());
    }
}