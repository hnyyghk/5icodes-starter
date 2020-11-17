package com._5icodes.starter.cache.config;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

@Slf4j
public class LettuceClientOptionsCustomizer implements LettuceClientConfigurationBuilderCustomizer {
    @Override
    public void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder) {
        clientConfigurationBuilder.clientOptions(
                ClusterClientOptions.builder()
                        .validateClusterNodeMembership(false)
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .topologyRefreshOptions(
                                ClusterTopologyRefreshOptions.builder()
                                        .enableAllAdaptiveRefreshTriggers()
                                        .build()
                        ).build()
        );
        log.info("customize lettuce client options");
    }
}