package com._5icodes.starter.redisson;

import com._5icodes.starter.cache.CacheAutoConfiguration;
import com._5icodes.starter.common.Initial;
import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.redisson.codec.RedissonKryoCodec;
import com._5icodes.starter.redisson.lock.LockAdvisor;
import com._5icodes.starter.redisson.lock.LockInterceptor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
public class RedissonAutoConfiguration {
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties,
                                         @Autowired(required = false) Initial<ClusterServersConfig> clusterInitial,
                                         @Autowired(required = false) Initial<SentinelServersConfig> sentinelInitial,
                                         @Autowired(required = false) Initial<SingleServerConfig> singleInitial,
                                         @Autowired(required = false) Initial<Config> configInitial) {
        Config config = prepareConfig(redisProperties, clusterInitial, sentinelInitial, singleInitial);
        config.setCodec(new RedissonKryoCodec());
        if (configInitial != null) {
            configInitial.init(config);
        }
        return Redisson.create(config);
    }

    private Config prepareConfig(RedisProperties redisProperties, Initial<ClusterServersConfig> clusterInitial, Initial<SentinelServersConfig> sentinelInitial, Initial<SingleServerConfig> singleInitial) {
        Config config = new Config();
        config.setNettyThreads(0);
        config.setThreads(0);
        String password = redisProperties.getPassword();
        Duration duration = redisProperties.getTimeout();
        int timeout = -1;
        if (duration != null) {
            timeout = ((Long) duration.toMillis()).intValue();
        }
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if (cluster != null) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            for (String node : cluster.getNodes()) {
                clusterServersConfig.addNodeAddress("redis://" + node);
            }
            if (StringUtils.hasText(password)) {
                clusterServersConfig.setPassword(password);
            }
            if (timeout > 0) {
                clusterServersConfig.setTimeout(timeout);
            }
            if (clusterInitial != null) {
                clusterInitial.init(clusterServersConfig);
            }
            return config;
        }
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel != null) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers().setMasterName(sentinel.getMaster());
            for (String node : sentinel.getNodes()) {
                sentinelServersConfig.addSentinelAddress("redis://" + node);
            }
            if (StringUtils.hasText(password)) {
                sentinelServersConfig.setPassword(password);
            }
            if (timeout > 0) {
                sentinelServersConfig.setTimeout(timeout);
            }
            if (sentinelInitial != null) {
                sentinelInitial.init(sentinelServersConfig);
            }
            return config;
        }
        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
        if (StringUtils.hasText(password)) {
            singleServerConfig.setPassword(password);
        }
        if (timeout > 0) {
            singleServerConfig.setTimeout(timeout);
        }
        if (singleInitial != null) {
            singleInitial.init(singleServerConfig);
        }
        return config;
    }

    @Bean
    public RedissonClientLifecycle redissonClientLifecycle(RedissonClient redissonClient) {
        return new RedissonClientLifecycle(redissonClient);
    }

    @Bean(RedissonConstants.LOCK_INTERCEPTOR_BEAN_NAME)
    public LockInterceptor lockInterceptor() {
        return new LockInterceptor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LockAdvisor lockAdvisor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        return new LockAdvisor(metadataReaderFactoryProvider);
    }
}