package com._5icodes.starter.eureka.monitor;

import com._5icodes.starter.monitor.meta.AbstractMetaInfoProvider;
import com.google.common.collect.Maps;
import com.netflix.appinfo.EurekaInstanceConfig;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.Map;

public class EurekaMetaInfoProvider extends AbstractMetaInfoProvider {
    private final EurekaInstanceConfig eurekaInstanceConfig;

    public EurekaMetaInfoProvider(EurekaInstanceConfig eurekaInstanceConfig) {
        this.eurekaInstanceConfig = eurekaInstanceConfig;
    }

    @Override
    protected Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(1);
        Map<String, String> metadataMap = eurekaInstanceConfig.getMetadataMap();
        result.put("meta", metadataMap);
        return result;
    }
}