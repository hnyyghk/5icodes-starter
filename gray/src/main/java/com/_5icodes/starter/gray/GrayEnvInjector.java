package com._5icodes.starter.gray;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.CommonConstants;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

/**
 * @see org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig
 */
public class GrayEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "spring.cloud.loadbalancer.ribbon.enabled", false);
        Binder binder = Binder.get(env);
        BindResult<GrayProperties> bindResult = binder.bind(CommonConstants.GRAY_PROPERTY_PREFIX, Bindable.of(GrayProperties.class));
        if (bindResult.isBound()) {
            GrayProperties grayProperties = bindResult.get();
            String region = grayProperties.getRegion();
            if (StringUtils.hasText(region)) {
                PropertySourceUtils.put(env, "spring.cloud.loadbalancer.zone", region);
            }
            String appGroup = grayProperties.getAppGroup();
            if (StringUtils.hasText(appGroup)) {
                PropertySourceUtils.put(env, "eureka.instance.metadata-map." + CommonConstants.APP_GROUP, appGroup);
            }
        }
        super.onAllProfiles(env, application);
    }
}