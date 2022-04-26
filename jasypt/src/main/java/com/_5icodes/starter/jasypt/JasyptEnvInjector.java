package com._5icodes.starter.jasypt;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class JasyptEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    private final String password = "jasypt.encryptor.password";

    /**
     * 如果将spring.application.name配置在bootstrap.yml中
     * 在调用env.getProperty("spring.application.name")时获取到的是null
     * 需手动配置jasypt.encryptor.bootstrap为false让jasypt对bootstrap.yml不做处理
     * @see com.ulisesbocchio.jasyptspringboot.JasyptSpringCloudBootstrapConfiguration
     * @see com.ulisesbocchio.jasyptspringboot.JasyptSpringBootAutoConfiguration
     * @see <a href="https://github.com/ulisesbocchio/jasypt-spring-boot/issues/256">issue#256</a>
     * @see <a href="https://github.com/ulisesbocchio/jasypt-spring-boot/issues/255">issue#255</a>
     *
     * @param env
     * @param application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        if (!shouldProcess(env, application)) {
            PropertySourceUtils.put(env, "jasypt.encryptor.bootstrap", false);
//            PropertySourceUtils.put(env, "jasypt.encryptor.skip-property-sources", OriginTrackedMapPropertySource.class.getName());
        }
        super.postProcessEnvironment(env, application);
    }

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, password, "test");
        super.onDev(env, application);
    }
}