package com._5icodes.starter.sharding.algorithms;

import com._5icodes.starter.sharding.config.ShardingConfiguration;
import com._5icodes.starter.sharding.utils.ActualDataNodesRegister;
import com._5icodes.starter.sharding.utils.MappingUtils;

/**
 * 默认使用16库1024表
 */
public class AvgActualDataNodesGenerator implements ActualDataNodesGenerator {
    @Override
    public String generateActualDataNodes(ShardingConfiguration shardingConfiguration, String tablePrefix) {
        int tableNum = shardingConfiguration.getTableEndIndex() - shardingConfiguration.getTableBeginIndex();
        int dbNum = shardingConfiguration.getDbEndIndex() - shardingConfiguration.getDbBeginIndex();
        //单库平均表数量
        int capacity = tableNum / dbNum;
        StringBuilder builder = new StringBuilder();
        for (int i = shardingConfiguration.getTableBeginIndex(); i < shardingConfiguration.getTableEndIndex(); i++) {
            String actualDataNode = AvgActualDataNodesGenerator.getActualDataNode(i / capacity + shardingConfiguration.getDbBeginIndex(),
                    shardingConfiguration.getDbPrefix(), shardingConfiguration.getDbFormatLength(),
                    i, tablePrefix, shardingConfiguration.getTableFormatLength());
            builder.append(actualDataNode).append(",");
        }
        ActualDataNodesRegister.getInstance().register(tablePrefix, shardingConfiguration);
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 根据前缀和值格式化数据分片
     *
     * @param db                db下标
     * @param dbPrefix          db前缀
     * @param dbFormatLength    db后缀
     * @param table             table下标
     * @param tablePrefix       table前缀
     * @param tableFormatLength table后缀
     * @return
     */
    public static String getActualDataNode(int db, String dbPrefix, Integer dbFormatLength, int table, String tablePrefix, Integer tableFormatLength) {
        String dbName = dbPrefix + String.format(MappingUtils.createFormat(dbFormatLength), db);
        String tableName = tablePrefix + String.format(MappingUtils.createFormat(tableFormatLength), table);
        return dbName + "." + tableName;
    }
}