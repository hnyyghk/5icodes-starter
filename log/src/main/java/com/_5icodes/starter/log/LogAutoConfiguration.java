package com._5icodes.starter.log;

import com._5icodes.starter.log.monitor.LogMetricStartListener;
import com._5icodes.starter.log.mybatis.MybatisLogConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MybatisLogConfiguration.class)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {
    @Bean
    public DynamicLogPropertiesChangeListener dynamicLogPropertiesChangeListener(LogProperties logProperties) {
        return new DynamicLogPropertiesChangeListener(logProperties);
    }

    @Bean
    public LogPropertiesProcessApplicationListener logPropertiesProcessApplicationListener(LogProperties logProperties) {
        return new LogPropertiesProcessApplicationListener(logProperties);
    }

    @Bean
    public LogMetricStartListener logMetricStartListener() {
        return new LogMetricStartListener();
    }
}