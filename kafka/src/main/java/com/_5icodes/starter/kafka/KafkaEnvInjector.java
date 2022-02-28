package com._5icodes.starter.kafka;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.kafka.trace.TraceTestProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class KafkaEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    private static final String KAFKA_BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";
    private static final String KAFKA_PRODUCER_PROPERTIES = "spring.kafka.producer.properties.";

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, KAFKA_BOOTSTRAP_SERVERS, "localhost:9092");
        super.onDev(env, application);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        //todo gray groupId
        PropertySourceUtils.put(env, "spring.kafka.consumer.group-id", SpringApplicationUtils.getApplicationName());
        PropertySourceUtils.put(env, KAFKA_PRODUCER_PROPERTIES + ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);
        PropertySourceUtils.put(env, KAFKA_PRODUCER_PROPERTIES + ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                TraceTestProducerInterceptor.class.getName());
        super.onAllProfiles(env, application);
    }
}