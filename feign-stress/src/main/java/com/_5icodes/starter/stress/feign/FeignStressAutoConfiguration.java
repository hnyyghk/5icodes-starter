package com._5icodes.starter.stress.feign;

import com._5icodes.starter.feign.client.CustomHttpClient;
import com._5icodes.starter.stress.feign.test.CustomStressHttpClient;
import com._5icodes.starter.stress.feign.test.remote.MockFeignAspect;
import com._5icodes.starter.stress.feign.test.remote.MockProperties;
import com._5icodes.starter.stress.feign.test.remote.MockServerStartListener;
import feign.Client;
import org.apache.http.client.HttpClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(com._5icodes.starter.feign.FeignAutoConfiguration.class)
@EnableConfigurationProperties(MockProperties.class)
@EnableFeignClients(basePackages = "com._5icodes.starter.stress.feign.test.local")
public class FeignStressAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Client feignClient(BlockingLoadBalancerClient loadBalancerClient, HttpClient httpClient) {
        CustomHttpClient delegate = new CustomHttpClient(httpClient);
        CustomStressHttpClient customStressHttpClient = new CustomStressHttpClient(delegate);
        return new FeignBlockingLoadBalancerClient(customStressHttpClient, loadBalancerClient);
    }

    @Bean
    public MockFeignAspect mockFeignAspect() {
        return new MockFeignAspect();
    }

    @Bean
    public MockServerStartListener mockServerStartListener(MockProperties mockProperties) {
        return new MockServerStartListener(mockProperties);
    }
}