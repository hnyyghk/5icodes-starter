package com._5icodes.starter.monitor;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

public class MonitorKafkaTemplate extends AbstractSmartLifecycle {
    private final KafkaTemplate<Object, Object> delegate;

    private final MonitorKafkaProperties monitorProperties;

    private Disruptor<MonitorDataWrapper> disruptor;

    private static volatile MonitorKafkaTemplate instance;

    public static MonitorKafkaTemplate getInstance() {
        return instance;
    }

    public MonitorKafkaTemplate(KafkaTemplate<Object, Object> delegate, MonitorKafkaProperties monitorProperties) {
        this.delegate = delegate;
        this.monitorProperties = monitorProperties;
    }

    public void send(String topic, String data) {
        disruptor.getRingBuffer().tryPublishEvent((event, sequence, arg0, arg1) -> {
            event.setTopic(arg0);
            event.setData(arg1);
        }, topic, data);
    }

    @Override
    public void doStart() {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("disruptor-kafka-%d").daemon(true).build();
        disruptor = new Disruptor<>(MonitorDataWrapper::new, monitorProperties.getRingBufferSize(), threadFactory, ProducerType.MULTI, new TimeoutBlockingWaitStrategy(3, TimeUnit.SECONDS));
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            if (monitorProperties.isEnabled()) {
                delegate.send(event.getTopic(), event.getData());
            }
            event.setTopic(null);
            event.setData(null);
        });
        disruptor.setDefaultExceptionHandler(new MonitorDataWrapperExceptionHandler());
        disruptor.start();
        instance = this;
    }

    @Override
    public void doStop() {
        try {
            disruptor.shutdown(1, TimeUnit.SECONDS);
        } catch (TimeoutException ignored) {
        }
    }
}