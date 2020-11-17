package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@UtilityClass
public class PropertySourceUtils {
    private final String CUSTOM_PROPERTIES = "custom.properties";

    public Map<String, Object> prepareOrGetMapSource(ConfigurableEnvironment environment, String sourceName, BiConsumer<MutablePropertySources, MapPropertySource> sourceLocationFunc) {
        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource mapPropertySource = (MapPropertySource) propertySources.get(sourceName);
        Map<String, Object> source;
        if (null == mapPropertySource) {
            source = new HashMap<>();
            mapPropertySource = new MapPropertySource(sourceName, source);
            sourceLocationFunc.accept(propertySources, mapPropertySource);
        } else {
            source = mapPropertySource.getSource();
        }
        return source;
    }

    protected Map<String, Object> prepareOrGetDefaultLocation(ConfigurableEnvironment environment) {
        return prepareOrGetMapSource(environment, CUSTOM_PROPERTIES, MutablePropertySources::addLast);
    }

    /**
     * 会被配置文件覆盖
     *
     * @param environment
     * @param name
     * @param value
     */
    public void put(ConfigurableEnvironment environment, String name, Object value) {
        Map<String, Object> location = prepareOrGetDefaultLocation(environment);
        location.put(name, value);
    }

    public Object get(ConfigurableEnvironment environment, String name) {
        Map<String, Object> location = prepareOrGetDefaultLocation(environment);
        return location.get(name);
    }

    private final String PRIORITY_PROPERTIES = "priority.properties";

    protected Map<String, Object> prepareOrGetPriorityLocation(ConfigurableEnvironment environment) {
        return prepareOrGetMapSource(environment, PRIORITY_PROPERTIES, MutablePropertySources::addFirst);
    }

    /**
     * 会覆盖配置文件
     *
     * @param environment
     * @param name
     * @param value
     */
    public void putPriority(ConfigurableEnvironment environment, String name, Object value) {
        Map<String, Object> location = prepareOrGetPriorityLocation(environment);
        location.put(name, value);
    }

    public Object getPriority(ConfigurableEnvironment environment, String name) {
        Map<String, Object> location = prepareOrGetPriorityLocation(environment);
        return location.get(name);
    }

    private final String EXCLUDE_NAME = "spring.autoconfigure.exclude";

    public void excludeAutoConfiguration(ConfigurableEnvironment environment, String name) {
        String property = environment.getProperty(EXCLUDE_NAME);
        if (StringUtils.hasText(property)) {
            putPriority(environment, EXCLUDE_NAME, property + "," + name);
        } else {
            putPriority(environment, EXCLUDE_NAME, name);
        }
    }
}