package com._5icodes.starter.webmvc;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.web.WebConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

public class WebMvcEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        String applicationName = SpringApplicationUtils.getApplicationName();
        String moduleKey = WebConstants.PROPERTY_PREFIX + ".module";
        if (!StringUtils.hasText(env.getProperty(moduleKey))) {
            PropertySourceUtils.put(env, moduleKey, applicationName);
        }
        super.onAllProfiles(env, application);
    }
}