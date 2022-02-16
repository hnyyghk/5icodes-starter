package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.ConfigurableEnvironment;

@UtilityClass
public class GrayUtils {
    private final String GRAY_ENABLE = "starter.meta.gray";

    private Boolean gray;

    public Boolean isGray() {
        return gray;
    }

    public void init(ConfigurableEnvironment environment) {
        if (gray != null) {
            return;
        }
        gray = environment.getProperty(GRAY_ENABLE, Boolean.class, false);
    }
}