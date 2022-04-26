package com._5icodes.starter.gray;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

public class RegionZoneMetaProvider implements InitializingBean {
    @Getter
    private String zone;
    @Getter
    private String region;

    private final Map<String, String> zoneRegionMap = new HashMap<>();

    public String getRegionByZone(String zone) {
        return zoneRegionMap.get(zone);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}