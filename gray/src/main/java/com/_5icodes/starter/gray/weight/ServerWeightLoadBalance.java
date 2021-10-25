package com._5icodes.starter.gray.weight;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

public interface ServerWeightLoadBalance {
    ServiceInstance choose(List<ServiceInstance> serverList);
}