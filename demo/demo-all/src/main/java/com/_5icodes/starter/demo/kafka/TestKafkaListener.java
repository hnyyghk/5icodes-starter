package com._5icodes.starter.demo.kafka;

import com._5icodes.starter.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestKafkaListener {
//    @KafkaListeners({@KafkaListener(topics = "TEST_TOPIC"), @KafkaListener(topics = "TEST_TOPIC_2")})
    @KafkaListener(topics = "TEST_TOPIC")
//    @KafkaListener(topics = "TEST_TOPIC_2")
    public void listen(ConsumerRecord<Object, Object> msg) {
        // 消费到数据后的处理逻辑
        log.info(JsonUtils.toJson(msg.value()));
    }
}