package com._5icodes.starter.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = WebConstants.PROPERTY_PREFIX)
public class WebProperties {
    private boolean autoWrap = true;

    private List<String> autoWrapExcludeClasses = new ArrayList<>();
}