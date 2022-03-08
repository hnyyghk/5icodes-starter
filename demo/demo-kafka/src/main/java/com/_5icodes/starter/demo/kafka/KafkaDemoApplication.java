package com._5icodes.starter.demo.kafka;

import com._5icodes.starter.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootApplication
@RestController
public class KafkaDemoApplication {
    public static void main(final String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @GetMapping("/kafka")
    public void testKafka(String topic) throws ExecutionException, InterruptedException {
        ListenableFuture future = kafkaTemplate.send(topic, "testKafkaProducerKey", "test");
        future.get();
    }

    @KafkaListeners({@KafkaListener(topics = "TEST_TOPIC"), @KafkaListener(topics = "TEST_TOPIC_2")})
//    @KafkaListener(topics = "TEST_TOPIC")
//    @KafkaListener(topics = "TEST_TOPIC_2")
    public void listen(ConsumerRecord<Object, Object> msg) {
        // 消费到数据后的处理逻辑
        log.info(JsonUtils.toJson(msg.value()));
    }
}