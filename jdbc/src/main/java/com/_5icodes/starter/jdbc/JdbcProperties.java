package com._5icodes.starter.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = JdbcConstants.PROPERTY_PREFIX)
@Data
public class JdbcProperties {
    private int maxResultSet = 1000;

    private boolean traceTestEnable = false;

    private Map<String, DruidDataSource> traceTestMap = new HashMap<>();

    private Monitor monitor = new Monitor();

    @Data
    public static class Monitor {
        private boolean enable = true;

        private int reportPeriod = 5;

        private String sqlTopicName = "T-DRUID-MONITOR-SQL";
    }
}