package com._5icodes.starter.stress.feign;

import com._5icodes.starter.feign.client.CustomHttpClient;
import com._5icodes.starter.stress.feign.test.CustomStressHttpClient;
import com._5icodes.starter.stress.feign.test.local.MockNotSupportProperties;
import com._5icodes.starter.stress.feign.test.local.MockSao;
import com._5icodes.starter.stress.feign.test.remote.MockFeignAspect;
import com._5icodes.starter.stress.feign.test.remote.MockProperties;
import com._5icodes.starter.stress.feign.test.remote.MockServerStartListener;
import feign.Client;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(com._5icodes.starter.feign.FeignAutoConfiguration.class)
@EnableConfigurationProperties({MockProperties.class, MockNotSupportProperties.class})
@EnableFeignClients(clients = MockSao.class)
public class FeignStressAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Client feignClient(HttpClient httpClient, @Autowired(required = false) BlockingLoadBalancerClient loadBalancerClient, LoadBalancerClientFactory loadBalancerClientFactory) {
        CustomHttpClient delegate = new CustomHttpClient(httpClient);
        CustomStressHttpClient customStressHttpClient = new CustomStressHttpClient(delegate);
        return new FeignBlockingLoadBalancerClient(customStressHttpClient, loadBalancerClient, loadBalancerClientFactory);
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