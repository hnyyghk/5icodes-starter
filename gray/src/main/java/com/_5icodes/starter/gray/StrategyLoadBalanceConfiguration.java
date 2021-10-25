package com._5icodes.starter.gray;

import com._5icodes.starter.gray.rule.RuleStrategyHolder;
import com._5icodes.starter.gray.rule.ZoneAvoidanceRuleDecorator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
public class StrategyLoadBalanceConfiguration {
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(
            ReactiveDiscoveryClient discoveryClient,
            Environment environment,
            ConfigurableApplicationContext context,
            LoadBalancerZoneConfig zoneConfig
    ) {
        ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context.getBeanProvider(LoadBalancerCacheManager.class);
        //开启服务实例缓存
        return new CachingServiceInstanceListSupplier(
                new ZoneRegionPriorityServerListFilter(
                        new DiscoveryClientServiceInstanceListSupplier(discoveryClient, environment),
                        zoneConfig
                ),
                cacheManagerProvider.getIfAvailable()
        );
    }

    @Bean
    public ReactorLoadBalancer<ServiceInstance> serviceInstanceReactorLoadBalancer(
            Environment environment,
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplier,
            RuleStrategyHolder strategyHolder
    ) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new ZoneAvoidanceRuleDecorator(serviceInstanceListSupplier, name, strategyHolder);
    }
}