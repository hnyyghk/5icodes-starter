package com._5icodes.starter.stress.feign;

public interface FeignStressConstants {
    String MODULE_NAME = "feign-stress";
    /**
     * mock prefix
     */
    String PROPERTY_PREFIX = "mock";
    String NOT_SUPPORT_PREFIX = FeignStressConstants.PROPERTY_PREFIX + ".not.support";
    /**
     * name
     */
    String NAME = "name=";
    /**
     * 空白
     */
    String WHITE_SPACE = "\\s*";
}