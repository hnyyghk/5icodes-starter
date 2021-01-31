package com._5icodes.starter.monitor.cache.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.TimeUtil;
import com._5icodes.starter.monitor.cache.CacheContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheMetricNodeRegister {
    public static void register(CacheContext cacheContext) {
        try {
            log.info(JsonUtils.toJson(cacheContext));
            CacheStatisticNode node = CacheClusterNode.getOrCreateOriginNode(cacheContext.getKey());
            Boolean exception = cacheContext.getException();
            if (null != exception && exception) {
                node.increaseExceptionQps(1);
            } else {
                node.addRtAndSuccess(TimeUtil.currentTimeMillis() - cacheContext.getStartTime(), 1);
            }
            //todo
        } finally {
            CacheContextUtils.removeCacheContext();
        }
    }
}