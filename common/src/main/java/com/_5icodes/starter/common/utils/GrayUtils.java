package com._5icodes.starter.common.utils;

import org.springframework.core.env.ConfigurableEnvironment;

public class GrayUtils {
    private final static String GRAY_ENABLE = "starter.meta.gray";

    private static Boolean gray;

    public static Boolean isGray() {
        if (gray == null) {
            ConfigurableEnvironment environment;
            try {
                environment = SpringUtils.getBean(ConfigurableEnvironment.class);
            } catch (Exception e) {
                return false;
            }
            gray = environment.getProperty(GRAY_ENABLE, Boolean.class, false);
        }
        return gray;
    }
}