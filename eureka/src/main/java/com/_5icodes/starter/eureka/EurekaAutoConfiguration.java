package com._5icodes.starter.eureka;

import com._5icodes.starter.eureka.monitor.EurekaMetaInfoProvider;
import com.netflix.appinfo.EurekaInstanceConfig;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
public class EurekaAutoConfiguration {
    @Bean
    public EurekaMetaInfoProvider eurekaMetaInfoProvider(EurekaInstanceConfig eurekaInstanceConfig) {
        return new EurekaMetaInfoProvider(eurekaInstanceConfig);
    }
}