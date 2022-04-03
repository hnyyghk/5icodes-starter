package com._5icodes.starter.jdbc.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatasourceTypeInfoRegistry {
    private static final DatasourceTypeInfoRegistry INSTANCE = new DatasourceTypeInfoRegistry();

    private DatasourceTypeInfoRegistry() {
    }

    public static DatasourceTypeInfoRegistry getInstance() {
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