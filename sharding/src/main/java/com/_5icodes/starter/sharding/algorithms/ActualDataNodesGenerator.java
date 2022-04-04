package com._5icodes.starter.sharding.algorithms;

import com._5icodes.starter.sharding.config.ShardingConfiguration;

/**
 * actualDataNodes生成接口
 */
public interface ActualDataNodesGenerator {
    /**
     * 生成数据分片
     *
     * @param shardingConfiguration 分片参数
     * @param tablePrefix           table前缀
     * @return
     */
    String generateActualDataNodes(ShardingConfiguration shardingConfiguration, String tablePrefix);
}