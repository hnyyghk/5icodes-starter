package com._5icodes.starter.saturn;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

@Slf4j
public class SaturnEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onLocal(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "saturn.enabled", false);
        processSaturnConfig(env, "http://127.0.0.1:9088");
        super.onLocal(env, application);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.excludeAutoConfiguration(env, QuartzAutoConfiguration.class.getName());
        super.onAllProfiles(env, application);
    }

    private void processSaturnConfig(ConfigurableEnvironment env, String consoleAddress) {
        String namespace = SpringApplicationUtils.getApplicationName() + (GrayUtils.isAppGroup() ? GrayUtils.getAppGroup() : "") + "Saturn";
        String executorName = null;
        Binder binder = Binder.get(env);
        BindResult<SaturnProperties> bindResult = binder.bind("saturn", SaturnProperties.class);
        if (bindResult.isBound()) {
            SaturnProperties saturnProperties = bindResult.get();
            if (StringUtils.hasText(saturnProperties.getNamespace())) {
                namespace = saturnProperties.getNamespace();
            }
            if (StringUtils.hasText(saturnProperties.getExecutorName())) {
                executorName = saturnProperties.getExecutorName();
            }
            if (StringUtils.hasText(saturnProperties.getConsoleAddress())) {
                consoleAddress = saturnProperties.getConsoleAddress();
            }
        }
        //设置系统默认环境
        System.setProperty("saturn.app.namespace", namespace);
        if (StringUtils.hasText(executorName)) {
            System.setProperty("saturn.app.executorName", executorName);
        }
        System.setProperty("VIP_SATURN_CONSOLE_URI", consoleAddress);
    }
}