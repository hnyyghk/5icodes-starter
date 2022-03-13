package com._5icodes.starter.apollo.listener;

import brave.ScopedSpan;
import com._5icodes.starter.common.utils.TraceUtils;
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

    /**
     * 优先触发EnvironmentChangeEvent, 因为可以触发Environment的连锁更新, 譬如jasypt解密
     * @see com.ulisesbocchio.jasyptspringboot.caching.RefreshScopeRefreshedEventListener#onApplicationEvent(ApplicationEvent)
     * 另外该事件会触发ConfigurationProperties的rebind
     * @see org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder#onApplicationEvent(EnvironmentChangeEvent)
     * 然后触发RefreshScope刷新, 因为可能将要刷新的bean依赖rebind properties
     * @see com._5icodes.starter.apollo.listener.RefreshScopeConfigChangeListener#onChange(ConfigChangeEvent)
     * 最后触发AutoUpdateConfigChangeListener刷新@Value注解的变量
     *
     * @param changeEvent
     */
    public void onChange(ConfigChangeEvent changeEvent) {
        //增加trace信息
        ScopedSpan span = TraceUtils.span("apollo-config");
        try {
            //apollo变更时间监听
            logValChange(changeEvent);
            for (ConfigChangeListener listener : listeners) {
                try {
                    listener.onChange(changeEvent);
                } catch (Exception e) {
                    log.error("fire changeEvent error", e);
                }
            }
        } finally {
            span.finish();
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