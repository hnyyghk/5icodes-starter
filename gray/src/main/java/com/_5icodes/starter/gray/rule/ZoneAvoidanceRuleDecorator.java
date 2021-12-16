package com._5icodes.starter.gray.rule;

import com._5icodes.starter.gray.utils.ServerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ZoneAvoidanceRuleDecorator extends RoundRobinLoadBalancer {
    private final RuleStrategyHolder strategyHolder;
    private final String serviceId;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public ZoneAvoidanceRuleDecorator(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, RuleStrategyHolder strategyHolder) {
        super(serviceInstanceListSupplierProvider, serviceId);
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.strategyHolder = strategyHolder;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get().next()
                .map(this::processInstanceResponse);
    }

    private Response<ServiceInstance> processInstanceResponse(List<ServiceInstance> serviceInstances) {
        if (CollectionUtils.isEmpty(serviceInstances)) {
            //注册中心无可用实例
            log.warn("no instance available {}", serviceId);
            return new EmptyResponse();
        }
        log.trace("eligible servers: {}", ServerUtils.getServerStr(serviceInstances));
        RuleStrategy ruleStrategy = strategyHolder.get(serviceId);
        ServiceInstance server = ruleStrategy.choose(serviceInstances);
        if (server != null) {
            log.debug("choose serviceId: {} server: {}", serviceId, ServerUtils.getServerStr(Collections.singletonList(server)));
        } else {
            log.debug("choose serviceId: {} with empty result", serviceId);
        }
        return new DefaultResponse(server);
    }
}