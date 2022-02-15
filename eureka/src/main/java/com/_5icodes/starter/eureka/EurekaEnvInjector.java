package com._5icodes.starter.eureka;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com.google.common.collect.ImmutableMap;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class EurekaEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    private static final String SERVICE_URL_PREFIX = "eureka.client.service-url.";
    private static final String AVAILABILITY_ZONES_PREFIX = "eureka.client.availability-zones.";
    private final static String EUREKA_INSTANCE_ZONE = "eureka.instance.metadata-map.zone";

    private final Map<String, Map<String, String>> SERVICE_URL_MAP = ImmutableMap.<String, Map<String, String>>builder()
            .put(AbstractProfileEnvironmentPostProcessor.DEV, ImmutableMap.<String, String>builder()
                    .put(RegionUtils.ZONE.CN_QINGDAO.getValue(), "http://localhost:8761/eureka/")
                    .put(RegionUtils.ZONE.CN_BEIJING.getValue(), "http://localhost:8762/eureka/")
                    .build())
            .build();

    @Override
    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
        initEureka(env, RegionUtils.ZONE.CN_BEIJING.getValue(), AbstractProfileEnvironmentPostProcessor.DEV);
        super.onDev(env, application);
    }

    private void initEureka(ConfigurableEnvironment env, String zone, String activeProfile) {
        Map<String, String> originServiceUrlMap = SERVICE_URL_MAP.get(activeProfile);
        Assert.notNull(originServiceUrlMap, "origin serviceUrlMap must not be null");
        Map<String, String> serviceUrlMap = new HashMap<>();
        for (Map.Entry<String, String> entry : originServiceUrlMap.entrySet()) {
            List<String> result = new ArrayList<>();
            String[] split = entry.getValue().split(",");
            //去重
            List<String> collect = Arrays.stream(split).distinct().collect(Collectors.toList());
            for (int i = collect.size(); i > 0; i--) {
                //防止负载不均衡
                int index = ThreadLocalRandom.current().nextInt(i);
                result.add(collect.get(index));
                collect.remove(index);
            }
            serviceUrlMap.put(entry.getKey(), String.join(",", result));
        }
        String serviceUrl = serviceUrlMap.get(zone);
        Assert.notNull(serviceUrl, "defaultZone serviceUrl must not be null");
        PropertySourceUtils.put(env, SERVICE_URL_PREFIX + EurekaClientConfigBean.DEFAULT_ZONE, serviceUrl);
        for (Map.Entry<String, String> entry : serviceUrlMap.entrySet()) {
            PropertySourceUtils.put(env, SERVICE_URL_PREFIX + entry.getKey(), entry.getValue());
        }
        Map<String, List<String>> collect = serviceUrlMap.keySet().stream().collect(Collectors.groupingBy(c -> c.split("-")[0]));
        for (Map.Entry<String, List<String>> entry : collect.entrySet()) {
            PropertySourceUtils.put(env, AVAILABILITY_ZONES_PREFIX + entry.getKey(), String.join(",", entry.getValue()));
        }
        PropertySourceUtils.put(env, "eureka.client.region", zone.split("-")[0]);
        PropertySourceUtils.put(env, EUREKA_INSTANCE_ZONE, zone);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        /**
         * 禁用EurekaClient的RefreshScope
         * @see org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration.RefreshableEurekaClientConfiguration
         * @see org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration.ConditionalOnRefreshScope
         * @see org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration.EurekaClientConfiguration
         * @see org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration.ConditionalOnMissingRefreshScope
         */
        PropertySourceUtils.put(env, "eureka.client.refresh.enable", false);
        PropertySourceUtils.put(env, "eureka.instance.prefer-ip-address", true);
        PropertySourceUtils.put(env, "eureka.instance.instance-id", "${spring.cloud.client.ip-address}:${server.port:8080}");
        PropertySourceUtils.put(env, "eureka.client.registry-fetch-interval-seconds", 10);
        //表示客户端从注册中心同步节点信息失败后，下次再次同步配置的时间间隔的最大倍数，默认值为10，将其设置为１，确保每次的同步时间都为固定值
        PropertySourceUtils.put(env, "eureka.client.cache-refresh-executor-exponential-back-off-bound", 1);
        super.onAllProfiles(env, application);
    }
}