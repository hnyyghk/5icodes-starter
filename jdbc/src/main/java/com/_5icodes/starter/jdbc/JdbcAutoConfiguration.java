package com._5icodes.starter.jdbc;

import com._5icodes.starter.jdbc.monitor.DruidFilterInitBeanPostProcessor;
import com._5icodes.starter.jdbc.monitor.DruidMetricCollector;
import com._5icodes.starter.jdbc.monitor.DruidMetricReporter;
import com._5icodes.starter.jdbc.monitor.JdbcMetaInfoProvider;
import com._5icodes.starter.jdbc.monitor.condition.ConditionalOnDruidMonitor;
import com._5icodes.starter.jdbc.trace.TraceTestDataSourcePostProcessor;
import com._5icodes.starter.jdbc.trace.condition.ConditionalOnTraceTestDataSource;
import com._5icodes.starter.monitor.MonitorKafkaTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcAutoConfiguration {
    @Bean
    public JdbcMetaInfoProvider jdbcMetaInfoProvider() {
        return new JdbcMetaInfoProvider();
    }

    @Configuration
    @ConditionalOnTraceTestDataSource
    public static class TraceTestDataSourceConfiguration {
        @Bean
        public TraceTestDataSourcePostProcessor traceTestDataSourcePostProcessor(JdbcProperties jdbcProperties) {
            return new TraceTestDataSourcePostProcessor(jdbcProperties);
        }
    }

    @Configuration
    @ConditionalOnDruidMonitor
    public static class DruidMonitorConfiguration {
        @Bean
        public static DruidFilterInitBeanPostProcessor druidFilterInitBeanPostProcessor() {
            return new DruidFilterInitBeanPostProcessor();
        }

        @Bean
        public DruidMetricCollector druidMetricCollector() {
            return new DruidMetricCollector();
        }

        @Bean
        public DruidMetricReporter druidMetricReporter(MonitorKafkaTemplate monitorKafkaTemplate,
                                                       DruidMetricCollector druidMetricCollector,
                                                       JdbcProperties jdbcProperties) {
            return new DruidMetricReporter(monitorKafkaTemplate, druidMetricCollector, jdbcProperties);
        }
    }
}