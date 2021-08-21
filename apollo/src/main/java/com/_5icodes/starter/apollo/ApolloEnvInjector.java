package com._5icodes.starter.apollo;

import com._5icodes.starter.apollo.utils.ApolloUtils;
import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.LinkedHashSet;
import java.util.Set;

public class ApolloEnvInjector extends AbstractProfileEnvironmentPostProcessor implements Ordered {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        String applicationName = SpringApplicationUtils.getApplicationName();
        PropertySourceUtils.put(env, PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, true);
        PropertySourceUtils.put(env, PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, true);
        PropertySourceUtils.put(env, "app.id", applicationName);
        PropertySourceUtils.put(env, "apollo.cacheDir", "/data/webapps/" + applicationName + "/conf");
        PropertySourceUtils.put(env, "logging.level.com.ctrip.framework.apollo.spring.property.AutoUpdateConfigChangeListener", "warn");
        processEnableApolloConfig(env, application);
        super.onAllProfiles(env, application);
    }

    protected void processEnableApolloConfig(ConfigurableEnvironment env, SpringApplication application) {
        if (env.containsProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES)) {
            return;
        }
        Set<String> namespaces = new LinkedHashSet<>();
        addNamespacesOnAnnotation(application, namespaces);
        addPreLoadNamespaces(namespaces);
        PropertySourceUtils.put(env, PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES, String.join(",", namespaces));
        env.getPropertySources().addLast(new PropertySource.StubPropertySource(PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME));
    }

    private void addNamespacesOnAnnotation(SpringApplication application, Set<String> namespaces) {
        Class<?> bootApplicationClass = SpringApplicationUtils.getBootApplicationClass(application);
        EnableApolloConfig apolloConfig = bootApplicationClass.getAnnotation(EnableApolloConfig.class);
        if (null == apolloConfig || ArrayUtils.isEmpty(apolloConfig.value())) {
            return;
        }
        for (String val : apolloConfig.value()) {
            namespaces.add(ApolloUtils.format(val));
        }
    }

    private void addPreLoadNamespaces(Set<String> namespaces) {
        namespaces.add(ApolloUtils.format(ConfigConsts.NAMESPACE_APPLICATION));
        namespaces.addAll(ApolloUtils.preLoadPublicNamespaces());
    }

    /**
     * @see https://github.com/ctripcorp/apollo/wiki/Java客户端使用指南#五本地开发模式
     */
    @Override
    protected void onIntegrationTest(ConfigurableEnvironment env, SpringApplication application) {
        System.setProperty("env", "LOCAL");
        super.onIntegrationTest(env, application);
    }

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        setApolloMetaLocation(env, "http://localhost:8080");
        //本地使用docker-quick-start不修改任何配置时需手动配置VM options为-Dapollo.configService=http://localhost:8080, 仅配置apollo.meta拉取的是容器内网IP
        System.setProperty("apollo.configService", "http://localhost:8080");
        super.onDev(env, application);
    }

    @Override
    protected void onPrd(ConfigurableEnvironment env, SpringApplication application) {
        setApolloMetaLocation(env, "http://113.104.209.69/apolloConfig");
        super.onPrd(env, application);
    }

    protected void setApolloMetaLocation(ConfigurableEnvironment env, String apolloMetaLocation) {
        PropertySourceUtils.put(env, "apollo.meta", apolloMetaLocation);
    }

    /**
     * 比apollo优先级高
     *
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#getOrder()
     */
    @Override
    public int getOrder() {
        return -1;
    }
}