package com._5icodes.starter.sentinel.monitor;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.StatisticNode;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @see com.alibaba.csp.sentinel.node.metric.MetricTimerListener
 * @see com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager
 */
public abstract class AbstractSentinelMetricSender implements BootApplicationListener<ApplicationStartedEvent> {
    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("sentinel-metrics-record-task", true));

    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        SCHEDULER.scheduleAtFixedRate(this::sendMetric, 0, 1, TimeUnit.SECONDS);
    }

    private void sendMetric() {
        Map<Long, List<Map<String, Object>>> maps = new TreeMap<>();
        for (Entry<ResourceWrapper, ClusterNode> e : ClusterBuilderSlot.getClusterNodeMap().entrySet()) {
            ClusterNode node = e.getValue();
            aggregate(maps, node, node.getName(), "");
            for (Entry<String, StatisticNode> nodeEntry : node.getOriginCountMap().entrySet()) {
                aggregate(maps, nodeEntry.getValue(), node.getName(), nodeEntry.getKey());
            }
        }
        aggregate(maps, Constants.ENTRY_NODE, Constants.ENTRY_NODE.getName(), "");
        if (maps.isEmpty()) {
            return;
        }
        for (Entry<Long, List<Map<String, Object>>> entry : maps.entrySet()) {
            List<Map<String, Object>> sentinelList = entry.getValue();
            for (Map<String, Object> sentinel : sentinelList) {
                doSend(sentinel);
            }
        }
    }

    private void aggregate(Map<Long, List<Map<String, Object>>> maps, StatisticNode node, String resourceName, String module) {
        Map<Long, MetricNode> metrics = node.metrics();
        for (Entry<Long, MetricNode> entry : metrics.entrySet()) {
            MetricNode metricNode = entry.getValue();
            Map<String, Object> sentinel = new HashMap<>();
            sentinel.put("t", metricNode.getTimestamp());
            sentinel.put("p", metricNode.getPassQps());
            sentinel.put("b", metricNode.getBlockQps());
            sentinel.put("s", metricNode.getSuccessQps());
            sentinel.put("e", metricNode.getExceptionQps());
            sentinel.put("r", metricNode.getRt());
            sentinel.put("n", resourceName);
            sentinel.put("m", module);
            sentinel.put("c", node.curThreadNum());
            sentinel.put("a", SpringApplicationUtils.getApplicationName());
            long time = entry.getKey();
            List<Map<String, Object>> nodes = maps.computeIfAbsent(time, k -> new ArrayList<>());
            nodes.add(sentinel);
        }
    }

    abstract void doSend(Map<String, Object> sentinel);
}