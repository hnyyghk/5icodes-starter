package com._5icodes.starter.rocketmq;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import org.apache.rocketmq.client.log.ClientLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class RocketmqEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    private final String nameSrvAddr = RocketmqConstants.PROPERTY_PREFIX + ".nameSrvAddr";

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, nameSrvAddr, "localhost:9876");
        super.onDev(env, application);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J, "true");
        PropertySourceUtils.put(env, RocketmqConstants.PROPERTY_PREFIX + ".group", SpringApplicationUtils.getApplicationName());
        PropertySourceUtils.put(env, "logging.level.RocketmqClient", "warn");
        PropertySourceUtils.put(env, "logging.level.RocketmqRemoting", "warn");
        super.onAllProfiles(env, application);
    }
}