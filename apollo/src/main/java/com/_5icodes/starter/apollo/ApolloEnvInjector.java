package com._5icodes.starter.apollo;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.LinkedHashSet;
import java.util.Set;

public class ApolloEnvInjector extends AbstractProfileEnvironmentPostProcessor implements Ordered {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        String appName = SpringApplicationUtils.getApplicationName();
        PropertySourceUtils.put(env, PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, true);
        PropertySourceUtils.put(env, PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, true);
        PropertySourceUtils.put(env, "app.id", appName);
        PropertySourceUtils.put(env, "apollo.cacheDir", "/data/webapps/" + appName + "/conf");
        PropertySourceUtils.put(env, "logging.level.com.ctrip.framework.apollo.spring.property.AutoUpdateConfigChangeListener", "warn");
        processEnableApolloConfig(env, application);
        super.onAllProfiles(env, application);
    }

    private void processEnableApolloConfig(ConfigurableEnvironment env, SpringApplication application) {
        if (env.containsProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES)) {
            return;
        }
        Set<String> namespaces = new LinkedHashSet<>();
        addNamespacesOnAnnotation(application, namespaces);
    }

    private void addNamespacesOnAnnotation(SpringApplication application, Set<String> namespaces) {
        EnableApolloConfig apolloConfig = SpringApplicationUtils.getBootApplicationClass(application).getAnnotation(EnableApolloConfig.class);
        if (null == apolloConfig || ArrayUtils.isEmpty(apolloConfig.value())) {
            return;
        }
        for (String val : apolloConfig.value()) {
            namespaces.add(val);
        }
    }

    @Override
    public int getOrder() {
        //比apollo优先级高
        return -1;
    }
}