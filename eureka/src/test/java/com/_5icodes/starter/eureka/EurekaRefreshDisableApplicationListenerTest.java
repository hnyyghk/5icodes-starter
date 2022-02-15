package com._5icodes.starter.eureka;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Constructor;

public class EurekaRefreshDisableApplicationListenerTest {
    @Test
    public void doContextPrepared() throws Exception {
        ConfigurableApplicationContext context = new GenericApplicationContext();
        injectMetaReaderFactory(context);

        EurekaRefreshDisableApplicationListener applicationListener = new EurekaRefreshDisableApplicationListener(Mockito.mock(SpringApplication.class), null);
        applicationListener.doContextPrepared(context);
        context.refresh();
        EurekaRefreshDisablePostProcessor eurekaRefreshDisablePostProcessor = context.getBean(EurekaRefreshDisablePostProcessor.class);

        Assert.assertNotNull(eurekaRefreshDisablePostProcessor.getMetadataReaderFactory());
    }

    private void injectMetaReaderFactory(ConfigurableApplicationContext context) throws Exception {
        Class<?> aClass = Class.forName("org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer");
        Constructor<?>[] constructors = aClass.getDeclaredConstructors();
        Constructor<?> constructor = constructors[0];
        constructor.setAccessible(true);
        ApplicationContextInitializer<ConfigurableApplicationContext> contextInitializer = (ApplicationContextInitializer<ConfigurableApplicationContext>) constructor.newInstance();
        contextInitializer.initialize(context);
    }
}