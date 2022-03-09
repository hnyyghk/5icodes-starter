package com._5icodes.starter.redisson;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class RedissonEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onIntegrationTest(ConfigurableEnvironment env, SpringApplication application) {
        /*
        集成测试环境下报错：
        2022-03-09 15:23:56.359 ERROR [5icodes-demo-redisson,,] 25076 --- [isson-netty-5-5] o.redisson.client.handler.CommandsQueue  : Exception occured. Channel: [id: 0xb2ac4930, L:/127.0.0.1:58009 - R:localhost/127.0.0.1:6379]

        java.io.IOException: 远程主机强迫关闭了一个现有的连接。
         */
        PropertySourceUtils.put(env, "logging.level.org.redisson.client.handler.CommandsQueue", "off");
        super.onIntegrationTest(env, application);
    }
}