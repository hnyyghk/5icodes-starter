package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@UtilityClass
public class SpringApplicationUtils {
    private final Map<SpringApplication, Boolean> APP_CACHE = new HashMap<>(2);
    private final Map<SpringApplication, List<String>> PACKAGE_CACHE = new HashMap<>();
    private static String applicationName;

    public boolean isBootApplication(SpringApplication application) {
        return APP_CACHE.computeIfAbsent(application, app -> {
            if (null == app) {
                return false;
            }
            Set<Object> sources = app.getAllSources();
            if (CollectionUtils.isEmpty(sources) || null == app.getMainApplicationClass()) {
                return false;
            }
            return sources.contains(app.getMainApplicationClass());
        });
    }

    public Class<?> getBootApplicationClass(SpringApplication application) {
        Assert.isTrue(isBootApplication(application), "application must not be bootstrap");
        return application.getMainApplicationClass();
    }

    public List<String> getBasePackages(SpringApplication application) {
        return PACKAGE_CACHE.computeIfAbsent(application, app -> {
            Class<?> bootApplicationClass = getBootApplicationClass(app);
            ComponentScan annotation = AnnotatedElementUtils.findMergedAnnotation(bootApplicationClass, ComponentScan.class);
            Set<String> packageSet = new HashSet<>();
            if (null != annotation) {
                if (0 != annotation.value().length) {
                    for (String s : annotation.value()) {
                        if (StringUtils.hasText(s)) {
                            packageSet.add(s);
                        }
                    }
                }
                if (0 != annotation.basePackages().length) {
                    for (String s : annotation.basePackages()) {
                        if (StringUtils.hasText(s)) {
                            packageSet.add(s);
                        }
                    }
                }
                if (0 != annotation.basePackageClasses().length) {
                    for (Class<?> aClass : annotation.basePackageClasses()) {
                        packageSet.add(aClass.getPackage().getName());
                    }
                }
            }
            if (packageSet.isEmpty()) {
                packageSet.add(bootApplicationClass.getPackage().getName());
            }
            return new ArrayList<>(packageSet);
        });
    }

    Map<ConfigurableEnvironment, List<String>> PACKAGE_EVN_CACHE = new HashMap<>();

    public void setBasePackages(ConfigurableEnvironment environment, List<String> packages) {
        PACKAGE_EVN_CACHE.put(environment, packages);
    }

    public List<String> getBasePackages(ConfigurableEnvironment environment) {
        return PACKAGE_EVN_CACHE.get(environment);
    }

    public String getApplicationName() {
        Assert.notNull(applicationName, "spring.application.name must not be null");
        return applicationName;
    }

    public void setApplicationName(String appName) {
        applicationName = appName;
    }

    public String getApplicationName(ConfigurableEnvironment env) {
        String appName = env.getProperty("spring.application.name");
        Assert.notNull(appName, "spring.application.name must not be null");
        return appName;
    }
}