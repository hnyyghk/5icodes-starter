package com._5icodes.starter.sleuth;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

public class SleuthEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "spring.sleuth.baggage.tag-fields", Arrays.asList(SleuthConstants.REQ_IP));
        PropertySourceUtils.put(env, "spring.sleuth.baggage.correlation-fields", Arrays.asList(SleuthConstants.REQ_IP));
        PropertySourceUtils.put(env, "spring.sleuth.baggage.remote-fields", Arrays.asList(SleuthConstants.REQ_IP));
        PropertySourceUtils.put(env, "spring.sleuth.sampler.rate", null);
        PropertySourceUtils.put(env, "spring.sleuth.sampler.probability", "1.0");
        super.onAllProfiles(env, application);
    }
}