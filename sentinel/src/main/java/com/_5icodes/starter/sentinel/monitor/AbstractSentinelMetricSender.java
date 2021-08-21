package com._5icodes.starter.sentinel.monitor;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSentinelMetricSender implements BootApplicationListener<ApplicationStartedEvent> {
    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private static final ScheduledExecutorService SENTINEL_SCHEDULER = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("sentinel-metrics-record-task", true));

    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        SENTINEL_SCHEDULER.scheduleAtFixedRate(this::sendMetric, 0, 1, TimeUnit.SECONDS);
    }

    private void sendMetric() {
        Map<Long, List<Map<String, Object>>> maps = new TreeMap<>();
        for (Map.Entry<ResourceWrapper, ClusterNode> entry : ClusterBuilderSlot.getClusterNodeMap().entrySet()) {
            aggregate(maps, entry.getValue(), entry.getKey().getName());
        }
        aggregate(maps, Constants.ENTRY_NODE, Constants.TOTAL_IN_RESOURCE_NAME);
        if (maps.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, List<Map<String, Object>>> entry : maps.entrySet()) {
            List<Map<String, Object>> sentinelList = entry.getValue();
            for (Map<String, Object> sentinel : sentinelList) {
                doSend(sentinel);
            }
        }
    }

    private void aggregate(Map<Long, List<Map<String, Object>>> maps, ClusterNode clusterNode, String resourceName) {
        Map<Long, MetricNode> metrics = clusterNode.metrics();
        for (Map.Entry<Long, MetricNode> entry : metrics.entrySet()) {
            MetricNode metricNode = entry.getValue();
            Map<String, Object> sentinel = new HashMap<>();
            sentinel.put("t", metricNode.getTimestamp());
            sentinel.put("p", metricNode.getPassQps());
            sentinel.put("b", metricNode.getBlockQps());
            sentinel.put("s", metricNode.getSuccessQps());
            sentinel.put("e", metricNode.getExceptionQps());
            sentinel.put("r", metricNode.getRt());
            sentinel.put("m", resourceName);
            sentinel.put("c", clusterNode.curThreadNum());
            sentinel.put("a", SpringApplicationUtils.getApplicationName());
            long time = entry.getKey();
            List<Map<String, Object>> sentinelList = maps.computeIfAbsent(time, k -> new ArrayList<>());
            sentinelList.add(sentinel);
        }
    }

    abstract void doSend(Map<String, Object> sentinel);
}