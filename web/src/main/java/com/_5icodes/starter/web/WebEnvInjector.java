package com._5icodes.starter.web;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

public class WebEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        String contextPath = WebConstants.COMMON_PREFIX + ".contextPath";
        if (!StringUtils.hasText(env.getProperty(contextPath))) {
            PropertySourceUtils.put(env, contextPath, env.getProperty("server.servlet.context-path"));
        }
        super.onAllProfiles(env, application);
    }
}