package com._5icodes.starter.webmvc;

import com._5icodes.starter.web.WebConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = WebConstants.PROPERTY_PREFIX)
public class WebMvcProperties {
    private String module;

    private String group;

    private boolean autoWrap = true;

    private Set<String> autoWrapExcludeClasses = new HashSet<>();

    private Set<String> allowList = new HashSet<>();

    private boolean authEnabled = false;
}