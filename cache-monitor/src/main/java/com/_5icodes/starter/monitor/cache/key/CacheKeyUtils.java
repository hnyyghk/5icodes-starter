package com._5icodes.starter.monitor.cache.key;

import com._5icodes.starter.monitor.cache.CacheContext;
import com._5icodes.starter.monitor.cache.CacheOperationType;
import com._5icodes.starter.monitor.cache.monitor.TimeUtil;

public class CacheKeyUtils {
    private static final ThreadLocal<CacheContext> CACHE_THREAD_LOCAL = new ThreadLocal<>();

    public static CacheContext getCacheContext() {
        return CACHE_THREAD_LOCAL.get();
    }

    public static void removeCacheContext() {
        CACHE_THREAD_LOCAL.remove();
    }

    private static void initContext(String key) {
        CacheContext cacheContext = new CacheContext(TimeUtil.currentTimeMillis(), key);
        CACHE_THREAD_LOCAL.set(cacheContext);
    }

    public static String format(String format, Object... args) {
        String key = String.format(format, args);
        initContext(key);
        return key;
    }

    public static String just(String key) {
        initContext(key);
        return key;
    }

    public static String prefix(String prefix, String arg) {
        initContext(prefix + "%s");
        return prefix + arg;
    }

    public static void setOperationType(CacheOperationType operationType) {
        CacheContext cacheKeyContext = getCacheContext();
        if (null != cacheKeyContext) {
            cacheKeyContext.setCacheOperationType(operationType);
        }
    }
}