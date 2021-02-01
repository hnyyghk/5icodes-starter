package com._5icodes.starter.apollo.listener;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

public class FireEnvironmentChangeEventConfigChangeListener implements ConfigChangeListener, ApplicationContextAware, PriorityOrdered {
    private ApplicationContext applicationContext;

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        //support change @ConfigurationProperties config without rerun
        applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}