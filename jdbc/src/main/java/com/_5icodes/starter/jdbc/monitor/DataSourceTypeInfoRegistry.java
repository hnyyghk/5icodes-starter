package com._5icodes.starter.jdbc.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceTypeInfoRegistry {
    private static final DataSourceTypeInfoRegistry INSTANCE = new DataSourceTypeInfoRegistry();

    private DataSourceTypeInfoRegistry() {
    }

    public static DataSourceTypeInfoRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, String> typeInfos = new ConcurrentHashMap<>();

    public void report(String sid, String dbType) {
        typeInfos.put(sid, dbType);
    }

    public Map<String, String> getTypeInfos() {
        return typeInfos;
    }
}