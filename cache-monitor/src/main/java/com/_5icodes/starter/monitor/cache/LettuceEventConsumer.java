package com._5icodes.starter.monitor.cache;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import io.lettuce.core.event.Event;
import io.lettuce.core.event.metrics.CommandLatencyEvent;
import io.lettuce.core.metrics.CommandLatencyId;
import io.lettuce.core.metrics.CommandMetrics;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LettuceEventConsumer implements InitializingBean {
    private final DefaultClientResources clientResources;

    public LettuceEventConsumer(DefaultClientResources clientResources) {
        this.clientResources = clientResources;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private void report(Event event) {
        if (!(event instanceof CommandLatencyEvent)) {
            return;
        }
        CommandLatencyEvent commandLatencyEvent = (CommandLatencyEvent) event;
        Map<CommandLatencyId, CommandMetrics> latencies = commandLatencyEvent.getLatencies();
        latencies.forEach((key, value) -> {
            Map<String, Object> reportMap = new HashMap<>();
            reportMap.put("app", SpringApplicationUtils.getApplicationName());
            reportMap.put("address", key.remoteAddress() == null ? "" : key.remoteAddress().toString().substring(1));
            reportMap.put("command", key.commandType().name());
            reportMap.put("qps", value.getCount());
            reportMap.put("maxRt", value.getCompletion().getMax() / 1000);
            reportMap.put("minRt", value.getCompletion().getMin() / 1000);
            reportMap.put("reportTime", System.currentTimeMillis());
            log.info(JsonUtils.toJson(reportMap));
        });
    }
}