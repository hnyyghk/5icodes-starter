package com._5icodes.starter.sharding;

import com._5icodes.starter.jdbc.utils.DataSourceTimezoneEditUtils;
import com._5icodes.starter.jdbc.utils.JdbcUrlResolveUtils;
import com._5icodes.starter.sharding.config.DataSourceConfiguration;
import com._5icodes.starter.sharding.config.ShardingConfiguration;
import com._5icodes.starter.sharding.constants.ModeEnum;
import com._5icodes.starter.sharding.constants.SourceTypeEnum;
import com._5icodes.starter.sharding.utils.DataSourceUtils;
import io.shardingjdbc.core.exception.ShardingJdbcException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class ShardingDataSourceFactoryBean implements FactoryBean<DataSource> {
    @Setter
    private String envPrefix;
    @Setter
    private Environment environment;
    @Setter
    private Integer sourceType;

    @Override
    public DataSource getObject() throws Exception {
        ShardingConfiguration shardingConfiguration = getShardingConfiguration();
        if (SourceTypeEnum.MASTER_ONLY.getCode().equals(sourceType)) {
            //支持分表、不支持分库、不支持读写分离
            shardingConfiguration.getDataSource().setSlave(null);
            shardingConfiguration.setMode(ModeEnum.SINGLE.getCode());
            return DataSourceUtils.createShardingDataSource(shardingConfiguration);
        } else if (SourceTypeEnum.MASTER_SLAVE.getCode().equals(sourceType)) {
            //支持分表、不支持分库、支持读写分离
            shardingConfiguration.setMode(ModeEnum.SINGLE.getCode());
            return DataSourceUtils.createShardingDataSource(shardingConfiguration);
        } else if (SourceTypeEnum.SHARDING.getCode().equals(sourceType)) {
            //支持分表、支持分库、支持读写分离
            return DataSourceUtils.createShardingDataSource(shardingConfiguration);
        } else {
            throw new ShardingJdbcException("unsupported sourceType, envPrefix: " + envPrefix + ", sourceType: " + sourceType);
        }
    }

    private ShardingConfiguration getShardingConfiguration() {
        Binder binder = Binder.get(environment);
        BindResult<ShardingConfiguration> bindResult = binder.bind(envPrefix, ShardingConfiguration.class);
        ShardingConfiguration shardingConfiguration = bindResult.get();
        if (shardingConfiguration == null
                || shardingConfiguration.getDataSource() == null
                || CollectionUtils.isEmpty(shardingConfiguration.getDataSource().getMaster())
                || !StringUtils.hasText(shardingConfiguration.getDataSource().getMaster().get(DataSourceUtils.USERNAME))
                || !StringUtils.hasText(shardingConfiguration.getDataSource().getMaster().get(DataSourceUtils.URL))) {
            throw new ShardingJdbcException("shardingConfiguration mapping fail, envPrefix: " + envPrefix);
        }

        DataSourceConfiguration dataSourceConfiguration = shardingConfiguration.getDataSource();

        Map<String, String> master = dataSourceConfiguration.getMaster();
        if (!CollectionUtils.isEmpty(master)) {
            String originUrl = master.get(DataSourceUtils.URL);
            JdbcUrlResolveUtils.resolve(originUrl).ifPresent(pair -> {
                if ("mysql".equals(pair.getLeft())) {
                    String editedUrl = DataSourceTimezoneEditUtils.editUrl(originUrl);
                    if (StringUtils.hasText(editedUrl) && !editedUrl.equals(originUrl)) {
                        master.put(DataSourceUtils.URL, editedUrl);
                    }
                }
            });
        }
        Map<String, String> slave = dataSourceConfiguration.getSlave();
        if (!CollectionUtils.isEmpty(slave)) {
            String originUrl = slave.get(DataSourceUtils.URL);
            JdbcUrlResolveUtils.resolve(originUrl).ifPresent(pair -> {
                if ("mysql".equals(pair.getLeft())) {
                    String editedUrl = DataSourceTimezoneEditUtils.editUrl(originUrl);
                    if (StringUtils.hasText(editedUrl) && !editedUrl.equals(originUrl)) {
                        slave.put(DataSourceUtils.URL, editedUrl);
                    }
                }
            });
        }
        return shardingConfiguration;
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }
}