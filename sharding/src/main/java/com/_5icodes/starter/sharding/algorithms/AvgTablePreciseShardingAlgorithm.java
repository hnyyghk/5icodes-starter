package com._5icodes.starter.sharding.algorithms;

import com._5icodes.starter.sharding.config.ShardingConfiguration;
import com._5icodes.starter.sharding.utils.ActualDataNodesRegister;
import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingjdbc.core.exception.ShardingJdbcException;

import java.util.Collection;

/**
 * 默认使用16库1024表
 */
public class AvgTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm {
    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        String tablePrefix = shardingValue.getLogicTableName();
        ShardingConfiguration shardingConfiguration = ActualDataNodesRegister.getInstance().getShardingConfiguration(tablePrefix);

        int tableNum = shardingConfiguration.getTableEndIndex() - shardingConfiguration.getTableBeginIndex();
        int dbNum = shardingConfiguration.getDbEndIndex() - shardingConfiguration.getDbBeginIndex();
        //单库平均表数量
        int capacity = tableNum / dbNum;

        long l;
        Object value = shardingValue.getValue();
        if (value instanceof Number) {
            l = ((Number) value).longValue();
        } else if (value instanceof String) {
            l = Long.parseLong((String) value);
        } else {
            throw new ShardingJdbcException("unsupported value type, shardingValue: " + shardingValue);
        }

        int i = (int) (l % tableNum);
        String actualDataNode = AvgActualDataNodesGenerator.getActualDataNode(i / capacity + shardingConfiguration.getDbBeginIndex(),
                shardingConfiguration.getDbPrefix(), shardingConfiguration.getDbFormatLength(),
                i, tablePrefix, shardingConfiguration.getTableFormatLength());

        String[] split = actualDataNode.split("\\.");
        String dbName = split[0];
        String tableName = split[1];
        for (Object availableTargetName : availableTargetNames) {
            if (tableName.equalsIgnoreCase((String) availableTargetName)) {
                return tableName;
            }
        }
        throw new ShardingJdbcException("couldn't find the target, expect dbName: " + dbName + ", expect tableName: " + tableName + ", shardingValue: " + shardingValue);
    }
}