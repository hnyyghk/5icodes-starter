package com._5icodes.starter.stress.feign.test.remote;

import lombok.Data;

import java.util.List;

@Data
public class MockApiDefine {
    /**
     * app
     */
    private String app;
    /**
     * 时间
     */
    private long timeout = 50L;
    /**
     * 全路径
     */
    private boolean mockAll = true;
    /**
     * 路径映射
     */
    private List<String> mapping;
}