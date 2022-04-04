package com._5icodes.starter.jdbc.monitor;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 修复数据库监控对sharding无法生效的bug
 */
public class DruidDataSourceFilterProcessor {
    private static final DruidDataSourceFilterProcessor INSTANCE = new DruidDataSourceFilterProcessor();

    private static final String STAT = "stat";

    private DruidDataSourceFilterProcessor() {
    }

    public static DruidDataSourceFilterProcessor getInstance() {
        return INSTANCE;
    }

    public void addFilters(DruidDataSource dataSource) {
        List<String> filterClassNames = dataSource.getFilterClassNames();
        List<String> monitorFilters = new LinkedList<>();
        monitorFilters.add(STAT);
        if (!CollectionUtils.isEmpty(filterClassNames)) {
            for (String filterClassName : filterClassNames) {
                if (filterClassName.equals(StatFilter.class.getName())) {
                    monitorFilters.remove(STAT);
                }
            }
        }
        if (!CollectionUtils.isEmpty(monitorFilters)) {
            StringBuilder builder = new StringBuilder();
            for (String monitorFilter : monitorFilters) {
                builder.append(monitorFilter).append(",");
            }
            String filterStr = builder.toString();
            try {
                dataSource.addFilters(filterStr.substring(0, filterStr.length() - 1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}