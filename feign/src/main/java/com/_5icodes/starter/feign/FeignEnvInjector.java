package com._5icodes.starter.feign;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.feign.custom.FeignClientCustom;
import com._5icodes.starter.feign.utils.FeignPropertyUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @see org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheProperties
 */
public class FeignEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "feign.auth.userName", "testUserName");
        PropertySourceUtils.put(env, "feign.auth.password", "testPassword");
        super.onDev(env, application);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "spring.main.allow-bean-definition-overriding", true);
        PropertySourceUtils.put(env, "feign.client.config.default.connectTimeout", FeignConstants.DEFAULT_CONNECT_TIMEOUT);
        PropertySourceUtils.put(env, "feign.client.config.default.readTimeout", FeignConstants.DEFAULT_READ_TIMEOUT);
        PropertySourceUtils.put(env, "spring.cloud.loadbalancer.cache.ttl", "3s");
        PropertySourceUtils.put(env, "feign.httpclient.follow-redirects", false);
        PropertySourceUtils.put(env, "feign.client.config.default.loggerLevel", "full");
        PropertySourceUtils.put(env, "spring.cloud.loadbalancer.maxRetriesOnNextServiceInstance", "0");
        Class<?> bootApplicationClass = SpringApplicationUtils.getBootApplicationClass(application);
        FeignClientCustom feignClientCustom = AnnotationUtils.findAnnotation(bootApplicationClass, FeignClientCustom.class);
        if (null != feignClientCustom) {
            FeignPropertyUtils.process(feignClientCustom, env, "", FeignClientCustom.class);
        }
        super.onAllProfiles(env, application);
    }
}