package com._5icodes.starter.monitor.meta;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMetaInfoSender implements MetaInfoSender {
    @Override
    public void send(List<MetaInfoProvider> metaInfoProviders) {
        if (CollectionUtils.isEmpty(metaInfoProviders)) {
            return;
        }
        Map<String, Object> metaInfo = new HashMap<>();
        for (MetaInfoProvider metaInfoProvider : metaInfoProviders) {
            metaInfo.putAll(metaInfoProvider.metaInfo());
        }
        doSend(metaInfo);
    }

    public abstract void doSend(Map<String, Object> metaInfo);
}