package com._5icodes.starter.gray.server;

import org.springframework.cloud.client.ServiceInstance;

public interface ServerPredicate {
    boolean test(ServiceInstance server);
}