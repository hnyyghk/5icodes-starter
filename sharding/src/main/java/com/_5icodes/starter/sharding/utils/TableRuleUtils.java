package com._5icodes.starter.sharding.utils;

import com._5icodes.starter.sharding.algorithms.*;
import com._5icodes.starter.sharding.config.ShardingConfiguration;
import com._5icodes.starter.sharding.config.TableRule;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.StandardShardingStrategyConfiguration;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

@UtilityClass
@Slf4j
public class TableRuleUtils {
    @SneakyThrows
    public void buildShardingRuleConfiguration(ShardingConfiguration shardingConfiguration, ShardingRuleConfiguration shardingRuleConfiguration) {
        List<TableRule> rules = shardingConfiguration.getTableRules();
        for (TableRule rule : rules) {
            TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration();
            tableRuleConfiguration.setLogicTable(rule.getLogicTable());
            //设置分片
            if (StringUtils.hasText(rule.getActualDataNodes())) {
                //使用配置的数据分片(一般是inline表达式)
                tableRuleConfiguration.setActualDataNodes(rule.getActualDataNodes());
            } else {
                String actualDataNodesClassName = rule.getActualDataNodesClassName();
                //如不配置分片算法，默认使用16库1024表
                if (!StringUtils.hasText(actualDataNodesClassName)) {
                    actualDataNodesClassName = AvgActualDataNodesGenerator.class.getName();
                }
                ActualDataNodesGenerator actualDataNodesGenerator = (ActualDataNodesGenerator) Class.forName(actualDataNodesClassName).newInstance();
                String actualDataNodes = actualDataNodesGenerator.generateActualDataNodes(shardingConfiguration, rule.getLogicTable());
                tableRuleConfiguration.setActualDataNodes(actualDataNodes);
            }

            //如果不配置分库分表sharding算法，默认使用16库1024表
            if (!StringUtils.hasText(rule.getDbShardingAlgorithmClassName())) {
                if (StringUtils.hasText(shardingConfiguration.getDefaultDbShardingAlgorithmClassName())) {
                    rule.setDbShardingAlgorithmClassName(shardingConfiguration.getDefaultDbShardingAlgorithmClassName());
                } else {
                    log.warn("no dbShardingAlgorithmClassName set");
                    rule.setDbShardingAlgorithmClassName(AvgDatabasePreciseShardingAlgorithm.class.getName());

                }
            }
            if (!StringUtils.hasText(rule.getTableShardingAlgorithmClassName())) {
                if (StringUtils.hasText(shardingConfiguration.getDefaultTableShardingAlgorithmClassName())) {
                    rule.setTableShardingAlgorithmClassName(shardingConfiguration.getDefaultTableShardingAlgorithmClassName());
                } else {
                    log.warn("no tableShardingAlgorithmClassName set");
                    rule.setTableShardingAlgorithmClassName(AvgTablePreciseShardingAlgorithm.class.getName());
                }
            }

            tableRuleConfiguration.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(rule.getShardingColumn(), rule.getDbShardingAlgorithmClassName()));
            tableRuleConfiguration.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(rule.getShardingColumn(), rule.getTableShardingAlgorithmClassName()));
            shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);
        }
    }
}