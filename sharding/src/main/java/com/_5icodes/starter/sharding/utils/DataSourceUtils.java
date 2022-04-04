package com._5icodes.starter.sharding.utils;

import com._5icodes.starter.jdbc.monitor.DataSourceTypeInfoRegistry;
import com._5icodes.starter.jdbc.monitor.DruidDataSourceFilterProcessor;
import com._5icodes.starter.jdbc.utils.JdbcUrlResolveUtils;
import com._5icodes.starter.sharding.config.ShardingConfiguration;
import com._5icodes.starter.sharding.constants.ModeEnum;
import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.MasterSlaveRuleConfiguration;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.exception.ShardingJdbcException;
import io.shardingjdbc.core.util.DataSourceUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@UtilityClass
public class DataSourceUtils {
    public static final String URL = "url";
    public static final String USERNAME = "username";
    /**
     * 默认master前缀
     */
    private static final String MASTER_PREFIX = "master_";
    /**
     * 默认slave前缀
     */
    private static final String SLAVE_PREFIX = "slave_";

    /**
     * 根据配置创建标准分库分表sharding连接池，具备读写分离能力
     *
     * @param shardingConfiguration
     * @return
     */
    @SneakyThrows
    public DataSource createShardingDataSource(ShardingConfiguration shardingConfiguration) {
        reportDataSourceMetaInfo(shardingConfiguration);
        Integer mode = shardingConfiguration.getMode();
        if (ModeEnum.AVG.getCode().equals(mode)) {
        } else if (ModeEnum.SINGLE.getCode().equals(mode)) {
            shardingConfiguration.setDbBeginIndex(0);
            shardingConfiguration.setDbEndIndex(1);
            shardingConfiguration.setDbFormatLength(0);
        } else {
            throw new ShardingJdbcException("unsupported mode, dbPrefix: " + shardingConfiguration.getDbPrefix() + ", mode: " + mode);
        }
        //创建数据库连接池及shardingRule配置
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        Map<String, DataSource> dataSourceMap = createAvgDataSource(shardingConfiguration, shardingRuleConfiguration);
        if (!CollectionUtils.isEmpty(shardingConfiguration.getTableRules())) {
            TableRuleUtils.buildShardingRuleConfiguration(shardingConfiguration, shardingRuleConfiguration);
        }
        //生成sharding-jdbc数据源
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, new ConcurrentHashMap<>(), shardingConfiguration.getProps());
    }

    /**
     * 多库多表主从平均分布模式
     *
     * @param shardingConfiguration
     * @param shardingRuleConfiguration
     * @return
     */
    @SneakyThrows
    private Map<String, DataSource> createAvgDataSource(ShardingConfiguration shardingConfiguration, ShardingRuleConfiguration shardingRuleConfiguration) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        Map<String, String> master = shardingConfiguration.getDataSource().getMaster();
        Map<String, String> slave = shardingConfiguration.getDataSource().getSlave();
        String masterUrl = master.get(URL);
        String slaveUrl = null;
        if (!CollectionUtils.isEmpty(slave)
                && StringUtils.hasText(slave.get(USERNAME))
                && StringUtils.hasText(slave.get(URL))) {
            slaveUrl = slave.get(URL);
        }
        for (int i = shardingConfiguration.getDbBeginIndex(); i < shardingConfiguration.getDbEndIndex(); i++) {
            String dbName = shardingConfiguration.getDbPrefix() + String.format(MappingUtils.createFormat(shardingConfiguration.getDbFormatLength()), i);
            //创建master连接池
            String masterDbName = MASTER_PREFIX + dbName;
            //替换成真正的url连接
            master.put(URL, masterUrl.replaceAll(shardingConfiguration.getDbPrefix(), dbName));
            DataSource masterDataSource = createDataSource(masterDbName, master);
            dataSourceMap.put(masterDbName, masterDataSource);

            //从库配置时使用主从方式，未配置时直接使用主库
            if (StringUtils.hasText(slaveUrl)) {
                //创建slave连接池
                String slaveDbName = SLAVE_PREFIX + dbName;
                //替换成真正的url连接
                slave.put(URL, slaveUrl.replaceAll(shardingConfiguration.getDbPrefix(), dbName));
                DataSource slaveDataSource = createDataSource(slaveDbName, slave);
                dataSourceMap.put(slaveDbName, slaveDataSource);

                //为db指定主从数据源
                MasterSlaveRuleConfiguration masterSlaveRuleConfiguration = new MasterSlaveRuleConfiguration();
                masterSlaveRuleConfiguration.setName(dbName);
                masterSlaveRuleConfiguration.setMasterDataSourceName(masterDbName);
                masterSlaveRuleConfiguration.getSlaveDataSourceNames().add(slaveDbName);

                //创建主从关系
                DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfiguration, new HashMap<>());
                shardingRuleConfiguration.getMasterSlaveRuleConfigs().add(masterSlaveRuleConfiguration);
                dataSourceMap.put(dbName, dataSource);
            } else {
                dataSourceMap.put(dbName, masterDataSource);
            }
        }
        return dataSourceMap;
    }

    /**
     * 根据配置创建普通连接池
     *
     * @param dataSourceName
     * @param dataSourceConfiguration
     * @return
     * @throws ReflectiveOperationException
     * @throws SQLException
     */
    @SneakyThrows
    private DataSource createDataSource(String dataSourceName, Map dataSourceConfiguration) {
        String dataSourceClassName = dataSourceConfiguration.getOrDefault("dataSourceClassName", DruidDataSource.class.getName()).toString();
        DataSource dataSource = DataSourceUtil.getDataSource(dataSourceClassName, dataSourceConfiguration);
        //手动触发init，不同连接池需要不同处理
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            DruidDataSourceFilterProcessor.getInstance().addFilters(druidDataSource);
            druidDataSource.setName(dataSourceName);
            druidDataSource.init();
        } else {
            log.warn("mismatch dataSourceClassName, manual initialization was not performed");
        }
        return dataSource;
    }

    private void reportDataSourceMetaInfo(ShardingConfiguration shardingConfiguration) {
        Map<String, String> master = shardingConfiguration.getDataSource().getMaster();
        if (CollectionUtils.isEmpty(master)) {
            return;
        }
        JdbcUrlResolveUtils.resolve(master.get(URL)).ifPresent(pair -> DataSourceTypeInfoRegistry.getInstance().report(pair.getRight(), pair.getLeft()));
    }
}