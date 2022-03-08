package com._5icodes.starter.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

import javax.annotation.PostConstruct;

public class CustomBean implements InitializingBean, SmartInitializingSingleton {
    private String desc;

    public CustomBean() {
        System.out.println("第二步: 执行CustomBean类的无参构造函数");
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        System.out.println("第三步: 调用setDesc方法");
        this.desc = desc;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("第六步: 调用afterPropertiesSet方法");
        this.desc = "在初始化方法中修改之后的描述信息";
    }

    public void initMethod() {
        System.out.println("第七步: 调用initMethod方法");
    }

    @PostConstruct
    public void postConstructMethod() {
        System.out.println("第五步: Post construct method");
    }

    @Override
    public String toString() {
        return "CustomBean{" +
                "desc='" + desc + '\'' +
                '}';
    }

    @Override
    public void afterSingletonsInstantiated() {
        System.out.println("第九步: after Singletons Instantiated");
    }
}