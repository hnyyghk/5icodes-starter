package com._5icodes.starter.monitor.cache;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.monitor.cache.monitor.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheMetricReporter extends AbstractSmartLifecycle implements Runnable {
    private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("redis-metric-report-%d").daemon(true).build());

    @Override
    public void doStart() {
        SCHEDULER.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void doStop() {
        SCHEDULER.shutdownNow();
    }

    @Override
    public void run() {
        Map<Long, List<Map<String, Object>>> maps = new TreeMap<>();
        for (Entry<String, CacheStatisticNode> entry : CacheClusterNode.getOriginCountMap().entrySet()) {
            CacheStatisticNode node = entry.getValue();
            Map<Long, CacheMetricNode> metrics = node.metrics();
            aggregate(maps, metrics, entry.getKey());
        }
        if (maps.isEmpty()) {
            return;
        }
        for (Entry<Long, List<Map<String, Object>>> entry : maps.entrySet()) {
            for (Map<String, Object> map : entry.getValue()) {
                map.put("reportTime", entry.getKey());
                map.put("app", SpringApplicationUtils.getApplicationName());
                log.info(JsonUtils.toJson(map));
            }
        }
    }

    private void aggregate(Map<Long, List<Map<String, Object>>> maps, Map<Long, CacheMetricNode> metrics, String keyName) {
        for (Entry<Long, CacheMetricNode> entry : metrics.entrySet()) {
            Long time = entry.getKey();
            CacheMetricNode metricNode = entry.getValue();
            maps.computeIfAbsent(time, k -> new ArrayList<>());
            List<Map<String, Object>> nodes = maps.get(time);
            Map<String, Object> node = new HashMap<>();
            node.put("keyName", keyName);
            node.put("rt", metricNode.getRt());
            node.put("successQps", metricNode.getSuccessQps());
            node.put("exceptionQps", metricNode.getExceptionQps());
            //todo
            for (CacheMetricEvent value : CacheMetricEvent.values()) {
                value.name();
            }
            nodes.add(node);
        }
    }
}