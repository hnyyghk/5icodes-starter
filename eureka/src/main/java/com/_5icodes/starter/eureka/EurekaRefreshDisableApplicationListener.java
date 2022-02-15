package com._5icodes.starter.eureka;

import com._5icodes.starter.common.application.ApplicationRunListenerAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class EurekaRefreshDisableApplicationListener extends ApplicationRunListenerAdapter {
    public EurekaRefreshDisableApplicationListener(SpringApplication application, String[] args) {
        super(application, args);
    }

    @Override
    protected void doContextPrepared(ConfigurableApplicationContext context) {
        RootBeanDefinition def = new RootBeanDefinition(EurekaRefreshDisablePostProcessor.class);
        def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        def.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        def.getPropertyValues().add("metadataReaderFactory",
                new RuntimeBeanReference("org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory"));
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        beanFactory.registerBeanDefinition("eurekaRefreshDisablePostProcessor", def);
        super.doContextPrepared(context);
    }
}