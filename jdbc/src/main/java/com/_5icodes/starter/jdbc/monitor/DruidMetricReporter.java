package com._5icodes.starter.jdbc.monitor;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.jdbc.JdbcProperties;
import com._5icodes.starter.jdbc.SqlMonitorRecord;
import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DruidMetricReporter extends AbstractSmartLifecycle {
    private final MonitorKafkaTemplate monitorKafkaTemplate;
    private final DruidMetricCollector druidMetricCollector;
    private final JdbcProperties jdbcProperties;

    private static final ScheduledExecutorService SCHEDULED = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("druid-monitor-report-%d").daemon(true).build());

    public DruidMetricReporter(MonitorKafkaTemplate monitorKafkaTemplate, DruidMetricCollector druidMetricCollector, JdbcProperties jdbcProperties) {
        this.monitorKafkaTemplate = monitorKafkaTemplate;
        this.druidMetricCollector = druidMetricCollector;
        this.jdbcProperties = jdbcProperties;
    }

    private void report() {
        List<SqlMonitorRecord> sqlMonitorRecordList = druidMetricCollector.collect();
        if (!CollectionUtils.isEmpty(sqlMonitorRecordList)) {
            for (SqlMonitorRecord sqlMonitorRecord : sqlMonitorRecordList) {
                String message = JsonUtils.toJson(sqlMonitorRecord);
                try {
                    monitorKafkaTemplate.send(jdbcProperties.getMonitor().getSqlTopicName(), message);
                } catch (Exception e) {
                    log.error("send sql message: {} to topic: {} fail", message, jdbcProperties.getMonitor().getSqlTopicName(), e);
                }
            }
        }

        sqlMonitorRecordList = druidMetricCollector.collectConnectInfo();
        if (!CollectionUtils.isEmpty(sqlMonitorRecordList)) {
            for (SqlMonitorRecord sqlMonitorRecord : sqlMonitorRecordList) {
                String message = JsonUtils.toJson(sqlMonitorRecord);
                try {
                    monitorKafkaTemplate.send(jdbcProperties.getMonitor().getSqlTopicName(), message);
                } catch (Exception e) {
                    log.error("send connect message: {} to topic: {} fail", message, jdbcProperties.getMonitor().getSqlTopicName(), e);
                }
            }
        }
    }

    @Override
    public void doStart() {
        SCHEDULED.scheduleAtFixedRate(this::report, jdbcProperties.getMonitor().getReportPeriod(), jdbcProperties.getMonitor().getReportPeriod(), TimeUnit.SECONDS);
    }

    @Override
    public void doStop() {
        SCHEDULED.shutdownNow();
    }
}