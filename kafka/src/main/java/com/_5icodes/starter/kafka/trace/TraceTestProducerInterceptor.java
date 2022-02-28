package com._5icodes.starter.kafka.trace;

import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.common.utils.SpringUtils;
import com._5icodes.starter.kafka.KafkaProperties;
import com._5icodes.starter.stress.StressConstants;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class TraceTestProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {
    private static final Field topicField;

    static {
        try {
            topicField = ProducerRecord.class.getDeclaredField("topic");
            topicField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SneakyThrows
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
        List<String> grayTopics = SpringUtils.getBean(KafkaProperties.class).getGrayTopics();
        if (GrayUtils.isAppGroup() && !CollectionUtils.isEmpty(grayTopics) && grayTopics.contains(record.topic())) {
            topicField.set(record, record.topic() + StressConstants.MQ_GRAY_SUFFIX);
        }
        if (TraceTestUtils.isTraceTest()) {
            topicField.set(record, record.topic() + StressConstants.MQ_STRESS_SUFFIX);
            TraceTestUtils.info("this is trace test kafka producer topic: {}", record.topic());
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}