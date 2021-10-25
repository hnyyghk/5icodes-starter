package com._5icodes.starter.gray.enums;

import com._5icodes.starter.gray.utils.ServerUtils;
import com._5icodes.starter.sleuth.SleuthConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;
import java.util.Set;

@Slf4j
public enum ServerMetaEnum {
    /**
     * 分区
     */
    REGION("region"),
    /**
     * 机房
     */
    ZONE("zone"),
    /**
     * 版本
     */
    VERSION("version"),
    /**
     * 环境分组
     */
    APP_GROUP(SleuthConstants.APP_GROUP),
    /**
     * 标签
     */
    TAGS("tags");

    @Getter
    private final String name;

    ServerMetaEnum(String name) {
        this.name = name;
    }

    public boolean predicateWhite(ServiceInstance server, Set<String> whiteList) {
        Set<String> valueList = ServerUtils.get(server, name);
        boolean predicate = contains(whiteList, valueList);
        log.trace("server host: {} port: {} valueList: {} whiteList: {} predicate: {}", server.getHost(), server.getPort(), valueList, whiteList, predicate);
        return predicate;
    }

    public boolean predicateBlack(ServiceInstance server, Set<String> blackList) {
        Set<String> valueList = ServerUtils.get(server, name);
        boolean predicate = !contains(blackList, valueList);
        log.trace("server host: {} port: {} valueList: {} blackList: {} predicate: {}", server.getHost(), server.getPort(), valueList, blackList, predicate);
        return predicate;
    }

    private boolean contains(Set<String> configList, Set<String> valueList) {
        for (String value : valueList) {
            if (configList.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public double getWeight(ServiceInstance server, Map<String, Double> weights, double left) {
        Set<String> valueList = ServerUtils.get(server, name);
        Double res = null;
        Double tmp;
        for (String value : valueList) {
            tmp = weights.get(value);
            if (tmp != null && (res == null || res < tmp)) {
                res = tmp;
            }
        }
        return res == null ? left : res;
    }

    public boolean test(ServiceInstance server, String meta) {
        Set<String> valueList = ServerUtils.get(server, name);
        return valueList.contains(meta);
    }
}