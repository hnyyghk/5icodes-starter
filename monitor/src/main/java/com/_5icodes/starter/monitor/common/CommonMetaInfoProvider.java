package com._5icodes.starter.monitor.common;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.monitor.meta.AbstractMetaInfoProvider;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.Map;

public class CommonMetaInfoProvider extends AbstractMetaInfoProvider {
    /**
     * 当前版本
     */
    @Value("${starter.version:}")
    private String starterVersion;
    /**
     * 打包时间
     */
    @Value("${starter.build.time:}")
    private String starterBuildTime;

    @Override
    protected Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(4);
        result.put("appName", SpringApplicationUtils.getApplicationName());
        result.put("startTime", System.currentTimeMillis());
        result.put("starterVersion", starterVersion);
        result.put("starterBuildTime", starterBuildTime);
        return result;
    }
}