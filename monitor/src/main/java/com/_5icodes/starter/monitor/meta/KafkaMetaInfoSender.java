package com._5icodes.starter.monitor.meta;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KafkaMetaInfoSender extends AbstractMetaInfoSender {
    @Override
    public void doSend(Map<String, Object> metaInfo) {
        metaInfo.put("zone", RegionUtils.getZone());
        //todo
        log.info(JsonUtils.toJson(metaInfo));
    }
}