package com._5icodes.starter.apollo.listener;

import com._5icodes.starter.apollo.ApolloConstants;
import com._5icodes.starter.apollo.utils.ApolloUtils;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import java.util.Set;

public class RefreshScopeConfigChangeListener implements ConfigChangeListener, ApplicationContextAware, Ordered {
    private ApplicationContext applicationContext;

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        String namespace = changeEvent.getNamespace();
        Set<String> namespaces = ApolloUtils.preLoadPublicNamespaces();
        //对于不在namespaces中或通用配置的刷新RefreshScope
        if (!namespaces.contains(namespace) || ApolloConstants.COMMON_NAME_SPACE.equals(namespace)) {
            applicationContext.getBean(RefreshScope.class).refreshAll();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}