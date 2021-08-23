package com._5icodes.starter.common;

import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class CommonEnvInjector extends AbstractProfileEnvironmentPostProcessor implements Ordered {
    @Override
    protected boolean shouldProcess(ConfigurableEnvironment env, SpringApplication application) {
        boolean shouldProcess = super.shouldProcess(env, application);
        if (!shouldProcess) {
            SpringApplicationUtils.setApplicationName(env.getProperty("spring.application.name"));
        }
        return shouldProcess;
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        //日期格式化
        PropertySourceUtils.put(env, "spring.jackson.date-format", "yyyy-MM-dd HH:mm:ss");
        PropertySourceUtils.put(env, "spring.jackson.time-zone", "GMT+8");
        PropertySourceUtils.put(env, "spring.jackson.serialization.write_dates_as_timestamps", false);
        //序列化时忽略为null的字段
        PropertySourceUtils.put(env, "spring.jackson.default-property-inclusion", JsonInclude.Include.NON_NULL);
        super.onAllProfiles(env, application);
    }

    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }
}