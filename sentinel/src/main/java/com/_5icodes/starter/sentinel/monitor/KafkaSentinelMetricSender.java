package com._5icodes.starter.sentinel.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KafkaSentinelMetricSender extends AbstractSentinelMetricSender {
    @Override
    void doSend(Map<String, Object> sentinel) {
        sentinel.put("zone", RegionUtils.getZone());
        //todo
        log.info(JsonUtils.toJson(sentinel));
    }
}