package com._5icodes.starter.rocketmq.sleuth;

import brave.propagation.Propagation;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.util.StringUtils;

public class RocketmqMessagePropagation implements Propagation.Setter<Message, String>, Propagation.Getter<MessageExt, String> {
    @Override
    public String get(MessageExt carrier, String key) {
        return carrier.getUserProperty(key);
    }

    @Override
    public void put(Message carrier, String key, String value) {
        if (StringUtils.hasText(value)) {
            carrier.putUserProperty(key, value);
        }
    }
}