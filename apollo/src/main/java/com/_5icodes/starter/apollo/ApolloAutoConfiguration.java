package com._5icodes.starter.apollo;

import com._5icodes.starter.apollo.listener.AutoUpdateWrapperConfigChangeListener;
import com._5icodes.starter.apollo.listener.FireEnvironmentChangeEventConfigChangeListener;
import com._5icodes.starter.apollo.listener.OrderedConfigChangeListener;
import com._5icodes.starter.apollo.listener.RefreshScopeConfigChangeListener;
import com.ctrip.framework.apollo.ConfigChangeListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApolloAutoConfiguration {
    @Bean
    public OrderedConfigChangeListener orderedConfigChangeListener(List<ConfigChangeListener> listeners) {
        return new OrderedConfigChangeListener(listeners);
    }

    @Bean
    public FireEnvironmentChangeEventConfigChangeListener fireEnvironmentChangeEventConfigChangeListener() {
        return new FireEnvironmentChangeEventConfigChangeListener();
    }

    @Bean
    public RefreshScopeConfigChangeListener refreshScopeConfigChangeListener() {
        return new RefreshScopeConfigChangeListener();
    }

    @Bean
    public AutoUpdateWrapperConfigChangeListener autoUpdateWrapperConfigChangeListener() {
        return new AutoUpdateWrapperConfigChangeListener();
    }
}