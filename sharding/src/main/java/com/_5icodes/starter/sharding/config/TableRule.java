package com._5icodes.starter.sharding.config;

import lombok.Data;

@Data
public class TableRule {
    /**
     * 逻辑表
     */
    private String logicTable;
    /**
     * 数据分片生成实现类类名
     */
    private String actualDataNodesClassName;
    /**
     * 数据分片inline表达式
     */
    private String actualDataNodes;
    /**
     * sharding列名，与db中列名一致
     */
    private String shardingColumn;
    /**
     * 分库算法实现类
     */
    private String dbShardingAlgorithmClassName;
    /**
     * 分表算法实现类
     */
    private String tableShardingAlgorithmClassName;
}