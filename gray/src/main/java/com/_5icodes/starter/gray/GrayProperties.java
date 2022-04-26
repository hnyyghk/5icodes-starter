package com._5icodes.starter.gray;

import com._5icodes.starter.common.CommonConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = CommonConstants.GRAY_PROPERTY_PREFIX)
public class GrayProperties {
    private String region;

    private String appGroup;
}