package com._5icodes.starter.stress.feign;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class FeignStressEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "mock.server", "");
        PropertySourceUtils.put(env, "mock.path", "");
        super.onDev(env, application);
    }
}