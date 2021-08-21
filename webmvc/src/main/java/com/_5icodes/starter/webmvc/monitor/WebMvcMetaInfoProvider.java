package com._5icodes.starter.webmvc.monitor;

import com._5icodes.starter.monitor.meta.AbstractMetaInfoProvider;
import com._5icodes.starter.webmvc.WebMvcProperties;
import com.google.common.collect.Maps;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.Map;

public class WebMvcMetaInfoProvider extends AbstractMetaInfoProvider {
    private final WebMvcProperties webMvcProperties;

    public WebMvcMetaInfoProvider(WebMvcProperties webMvcProperties) {
        this.webMvcProperties = webMvcProperties;
    }

    @Override
    protected Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event) {
        Map<String, Object> metaInfo = Maps.newHashMapWithExpectedSize(1);
        metaInfo.put("allowList", webMvcProperties.getAllowList());
        return metaInfo;
    }
}