package com._5icodes.starter.sharding.config;

import lombok.Data;

import java.util.Map;

@Data
public class DataSourceConfiguration {
    /**
     * 主库参数集
     */
    private Map<String, String> master;
    /**
     * 从库参数集
     */
    private Map<String, String> slave;
}