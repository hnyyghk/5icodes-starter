package com._5icodes.starter.demo;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class TestEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        System.out.println(PropertySourceUtils.get(env,"test"));
        System.out.println(PropertySourceUtils.getPriority(env,"test"));
        System.out.println(env.getProperty("test"));
        System.out.println("----------------------");
        PropertySourceUtils.put(env,"test","test1");
        System.out.println(PropertySourceUtils.get(env,"test"));
        System.out.println(PropertySourceUtils.getPriority(env,"test"));
        System.out.println(env.getProperty("test"));
        System.out.println("----------------------");
        PropertySourceUtils.putPriority(env,"test","test2");
        System.out.println(PropertySourceUtils.get(env,"test"));
        System.out.println(PropertySourceUtils.getPriority(env,"test"));
        System.out.println(env.getProperty("test"));
        System.out.println("----------------------");
        PropertySourceUtils.put(env,"test","test3");
        System.out.println(PropertySourceUtils.get(env,"test"));
        System.out.println(PropertySourceUtils.getPriority(env,"test"));
        System.out.println(env.getProperty("test"));
        System.out.println("----------------------");
        PropertySourceUtils.putPriority(env,"test","test4");
        System.out.println(PropertySourceUtils.get(env,"test"));
        System.out.println(PropertySourceUtils.getPriority(env,"test"));
        System.out.println(env.getProperty("test"));
        System.out.println("----------------------");
        super.onDev(env, application);
    }
}