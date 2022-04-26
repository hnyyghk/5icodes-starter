package com._5icodes.starter.common.utils;

import com._5icodes.starter.common.CommonConstants;
import lombok.experimental.UtilityClass;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

@UtilityClass
public class GrayUtils {
    private String appGroup;

    public Boolean isAppGroup() {
        return StringUtils.hasText(appGroup);
    }

    public static String getAppGroup() {
        return appGroup;
    }

    public void init(ConfigurableEnvironment environment) {
        appGroup = environment.getProperty(CommonConstants.GRAY_PROPERTY_PREFIX + "." + CommonConstants.APP_GROUP);
    }
}