package com._5icodes.starter.apollo.listener;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.internals.AbstractConfig;
import com.ctrip.framework.apollo.internals.DefaultConfig;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySourceFactory;
import com.ctrip.framework.apollo.spring.property.AutoUpdateConfigChangeListener;
import com.ctrip.framework.apollo.spring.util.SpringInjector;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

public class AutoUpdateWrapperConfigChangeListener implements ConfigChangeListener, Ordered {
    private AutoUpdateConfigChangeListener delegate;

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        delegate.onChange(changeEvent);
    }

    /**
     * 由于fireConfigChange中m_executorService使用的是newCachedThreadPool
     * 各ConfigChangeListener在不同线程中执行onChange方法
     * 不能保证FireEnvironmentChangeEventConfigChangeListener与RefreshScopeConfigChangeListener先于AutoUpdateConfigChangeListener执行完
     * 导致AutoUpdateConfigChangeListener在刷新@Value时获取到的是jasypt解密前的值
     * @see com.ctrip.framework.apollo.internals.AbstractConfig#fireConfigChange(ConfigChangeEvent)
     */
    @PostConstruct
    @SneakyThrows
    public void init() {
        Field listenersField = AbstractConfig.class.getDeclaredField("m_listeners");
        listenersField.setAccessible(true);
        ConfigPropertySourceFactory configPropertySourceFactory = SpringInjector.getInstance(ConfigPropertySourceFactory.class);
        List<ConfigPropertySource> configPropertySources = configPropertySourceFactory.getAllConfigPropertySources();
        for (ConfigPropertySource configPropertySource : configPropertySources) {
            String namespace = configPropertySource.getName();
            Config config = ConfigService.getConfig(namespace);
            if (!(config instanceof DefaultConfig)) {
                continue;
            }
            List<ConfigChangeListener> listeners = (List<ConfigChangeListener>) listenersField.get(config);
            listeners.stream().filter(listener -> listener instanceof AutoUpdateConfigChangeListener)
                    .findFirst().ifPresent(autoUpdateConfigChangeListener -> {
                listeners.remove(autoUpdateConfigChangeListener);
                delegate = (AutoUpdateConfigChangeListener) autoUpdateConfigChangeListener;
            });
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}