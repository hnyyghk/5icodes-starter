package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

@UtilityClass
public class SpringUtils {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return applicationContext.getBean(beanName, requiredType);
    }
}