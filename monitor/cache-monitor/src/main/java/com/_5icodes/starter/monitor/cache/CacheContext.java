package com._5icodes.starter.monitor.cache;

import lombok.Data;

@Data
public class CacheContext {
    private final long startTime;
    private final String key;
    private CacheOperationType cacheOperationType;
    private Integer valueSize;
}