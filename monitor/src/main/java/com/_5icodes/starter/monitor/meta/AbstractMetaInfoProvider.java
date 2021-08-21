package com._5icodes.starter.monitor.meta;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.Map;

public abstract class AbstractMetaInfoProvider implements MetaInfoProvider, BootApplicationListener<ApplicationStartedEvent> {
    private Map<String, Object> metaInfo;

    @Override
    public Map<String, Object> metaInfo() {
        return metaInfo;
    }

    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        metaInfo = doGetMetaInfo(event);
    }

    protected abstract Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event);
}