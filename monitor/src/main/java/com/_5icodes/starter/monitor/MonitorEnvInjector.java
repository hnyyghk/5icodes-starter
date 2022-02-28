package com._5icodes.starter.monitor;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class MonitorEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    private static final String MONITOR_BOOTSTRAP_SERVERS = MonitorConstants.PROPERTY_PREFIX + ".bootstrap-servers";

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, MONITOR_BOOTSTRAP_SERVERS, "localhost:9092");
        super.onLocal(env, application);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, MonitorConstants.PROPERTY_PREFIX + ".properties." + ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);
        super.onAllProfiles(env, application);
    }
}