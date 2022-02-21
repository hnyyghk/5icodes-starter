package com._5icodes.starter.rocketmq.producer;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.BeanInitializationException;

public class RocketmqProducerContainer extends AbstractSmartLifecycle {
    private final DefaultMQProducer producer;

    public RocketmqProducerContainer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    @Override
    public void doStart() {
        try {
            producer.start();
        } catch (MQClientException e) {
            throw new BeanInitializationException("start rocketmq producer failed", e);
        }
    }

    @Override
    public void doStop() {
        producer.shutdown();
    }
}