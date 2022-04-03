package com._5icodes.starter.monitor.meta;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
        metaInfo.put("zone", RegionUtils.getZone());
        log.info("metaInfo is: {}", JsonUtils.toJson(metaInfo));
        doSend(metaInfo);
    }

    public abstract void doSend(Map<String, Object> metaInfo);
}