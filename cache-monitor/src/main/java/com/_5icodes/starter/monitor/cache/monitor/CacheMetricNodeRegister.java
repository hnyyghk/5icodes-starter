package com._5icodes.starter.monitor.cache.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.monitor.cache.CacheContext;
import com._5icodes.starter.monitor.cache.CacheOperationType;
import com._5icodes.starter.monitor.cache.key.CacheKeyUtils;
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
            Integer valueSize = cacheContext.getValueSize();
            boolean getReq = cacheContext.getCacheOperationType() == CacheOperationType.GET;
            boolean empty = getReq && valueSize == 0;
            //todo
        } finally {
            CacheKeyUtils.removeCacheContext();
        }
    }
}