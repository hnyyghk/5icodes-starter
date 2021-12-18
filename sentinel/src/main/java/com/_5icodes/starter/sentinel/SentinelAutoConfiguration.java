package com._5icodes.starter.sentinel;

import com._5icodes.starter.sentinel.monitor.AbstractSentinelMetricSender;
import com._5icodes.starter.sentinel.monitor.KafkaSentinelMetricSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
public class SentinelAutoConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public AbstractSentinelMetricSender metricTimerListener() {
		return new KafkaSentinelMetricSender();
	}
}