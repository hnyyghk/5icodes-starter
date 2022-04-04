package com._5icodes.starter.sharding.config;

import com._5icodes.starter.sharding.algorithms.AvgDatabasePreciseShardingAlgorithm;
import com._5icodes.starter.sharding.algorithms.AvgTablePreciseShardingAlgorithm;
import lombok.Data;

import java.util.List;
import java.util.Properties;

@Data
public class ShardingConfiguration {
    /**
     * db前缀
     */
    private String dbPrefix;
    /**
     * 数据库配置
     */
    private DataSourceConfiguration dataSource;
    /**
     * 表规则配置列表
     */
    private List<TableRule> tableRules;
    /**
     * sharding-jdbc的参数配置
     */
    private Properties props;
    /**
     * 分布模式(0-多库多表主从平均分布模式，1-单库多表模式，默认是多库多表主从平均分布模式)
     */
    private Integer mode = 1;
    /**
     * db起始值，包含
     */
    private Integer dbBeginIndex = 0;
    /**
     * db结束值，不包含
     */
    private Integer dbEndIndex = 16;
    /**
     * db格式化长度，默认是3
     */
    private Integer dbFormatLength = 3;
    /**
     * table起始值，包含
     */
    private Integer tableBeginIndex = 0;
    /**
     * table结束值，不包含
     */
    private Integer tableEndIndex = 1024;
    /**
     * table格式化长度，默认是4
     */
    private Integer tableFormatLength = 4;
    /**
     * 默认分库算法实现类
     */
    private String defaultDbShardingAlgorithmClassName = AvgDatabasePreciseShardingAlgorithm.class.getName();
    /**
     * 默认分表算法实现类
     */
    private String defaultTableShardingAlgorithmClassName = AvgTablePreciseShardingAlgorithm.class.getName();
}