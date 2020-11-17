package com._5icodes.starter.monitor.cache.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.monitor.cache.CacheContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheMetricNodeRegister {
    public static void register(CacheContext cacheContext) {
        try {
            //todo
            log.info(JsonUtils.toJson(cacheContext));
        } finally {
            CacheContextUtils.removeCacheContext();
        }
    }
}