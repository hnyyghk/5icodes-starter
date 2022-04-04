package com._5icodes.starter.sharding.utils;

import com._5icodes.starter.sharding.config.ShardingConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActualDataNodesRegister {
    private static final ActualDataNodesRegister INSTANCE = new ActualDataNodesRegister();

    private ActualDataNodesRegister() {
    }

    public static ActualDataNodesRegister getInstance() {
        return INSTANCE;
    }

    private final Map<String, ShardingConfiguration> registry = new ConcurrentHashMap<>();

    public void register(String logicTableName, ShardingConfiguration shardingConfiguration) {
        registry.put(logicTableName.toLowerCase(), shardingConfiguration);
    }

    public ShardingConfiguration getShardingConfiguration(String logicTableName) {
        return registry.get(logicTableName.toLowerCase());
    }
}