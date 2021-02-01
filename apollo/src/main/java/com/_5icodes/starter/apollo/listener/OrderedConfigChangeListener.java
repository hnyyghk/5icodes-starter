package com._5icodes.starter.apollo.listener;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import com.ctrip.framework.apollo.spring.config.ConfigPropertySourceFactory;
import com.ctrip.framework.apollo.spring.util.SpringInjector;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
public class OrderedConfigChangeListener {
    private final List<ConfigChangeListener> listeners;

    public OrderedConfigChangeListener(List<ConfigChangeListener> listeners) {
        AnnotationAwareOrderComparator.sort(listeners);
        this.listeners = listeners;
    }

    public void onChange(ConfigChangeEvent changeEvent) {
        logValChange(changeEvent);
        for (ConfigChangeListener listener : listeners) {
            listener.onChange(changeEvent);
        }
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        ConfigPropertySourceFactory configPropertySourceFactory = SpringInjector.getInstance(ConfigPropertySourceFactory.class);
        List<ConfigPropertySource> configPropertySources = configPropertySourceFactory.getAllConfigPropertySources();
        for (ConfigPropertySource configPropertySource : configPropertySources) {
            configPropertySource.addChangeListener(this::onChange);
        }
    }

    private void logValChange(ConfigChangeEvent changeEvent) {
        changeEvent.changedKeys().forEach(item -> {
            if (changeEvent.isChanged(item)) {
                ConfigChange change = changeEvent.getChange(item);
                if (null == change) {
                    return;
                }
                log.info("apollo dynamic changed namespace: {} key: {} oldValue: {} newValue: {}", change.getNamespace(), item, change.getOldValue(), change.getNewValue());
            }
        });
    }
}