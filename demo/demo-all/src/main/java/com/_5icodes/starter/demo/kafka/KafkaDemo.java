package com._5icodes.starter.demo.kafka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class KafkaDemo {
    private static final String TEST_TOPIC = "TEST_TOPIC";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @SneakyThrows
    @GetMapping("/kafka")
    public void testKafka(@RequestParam(required = false, defaultValue = TEST_TOPIC, name = "topic") String topic) {
        kafkaTemplate.send(topic, RandomStringUtils.randomAlphanumeric(10));
    }

    @KafkaListeners({@KafkaListener(topics = TEST_TOPIC), @KafkaListener(topics = "TEST_TOPIC_2")})
//    @KafkaListener(topics = TEST_TOPIC)
//    @KafkaListener(topics = "TEST_TOPIC_2")
    public void listen(ConsumerRecord<Object, Object> msg) {
        // 消费到数据后的处理逻辑
        log.info("get data: {}", msg.value());
    }
}