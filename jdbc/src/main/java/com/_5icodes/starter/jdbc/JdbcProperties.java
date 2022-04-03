package com._5icodes.starter.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = JdbcConstants.PROPERTY_PREFIX)
@Data
public class JdbcProperties {
    /**
     * 最大查询数据记录大小，默认1000
     */
    private int maxResultSet = 1000;

    private boolean traceTestEnable = false;

    private Map<String, DruidDataSource> traceTestMap = new HashMap<>();

    private Monitor monitor = new Monitor();

    @Data
    public static class Monitor {
        /**
         * 是否开启监控，默认true
         */
        private boolean enable = true;

        /**
         * 每次上报监控数据间隔时间，默认5秒
         */
        private int reportPeriod = 5;

        private String sqlTopicName = "T-MONITOR-DRUID";
    }
}