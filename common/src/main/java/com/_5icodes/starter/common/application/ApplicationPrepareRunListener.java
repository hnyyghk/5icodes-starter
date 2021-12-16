package com._5icodes.starter.common.application;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.CommonConstants;
import com._5icodes.starter.common.exception.MissingModuleException;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class ApplicationPrepareRunListener extends ApplicationRunListenerAdapter implements PriorityOrdered {
    private static final Map<String, String> STARTER_CLASS_ARTIFACT_ID_MAP = new HashMap<>();
    private static final Map<String, Map<String, String>> CONDITIONAL_CLASS_ARTIFACT_ID_MAP = new HashMap<>();

    static {
        STARTER_CLASS_ARTIFACT_ID_MAP.put("com._5icodes.starter.common.loader.CommonModuleNameProvider", CommonConstants.MODULE_NAME);
        CONDITIONAL_CLASS_ARTIFACT_ID_MAP.put("com._5icodes.starter.common.loader.CommonModuleNameProvider",
                Collections.singletonMap("com._5icodes.starter.common.loader.CommonModuleNameProvider", CommonConstants.MODULE_NAME));
    }

    public ApplicationPrepareRunListener(SpringApplication application, String[] args) {
        super(application, args);
    }

    @Override
    protected void doEnvironmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        checkModules();
        setDefaultActiveProfile(environment);
        injectBasePackages2Environment(environment);
        super.doEnvironmentPrepared(bootstrapContext, environment);
    }

    private void injectBasePackages2Environment(ConfigurableEnvironment environment) {
        List<String> packages = SpringApplicationUtils.getBasePackages(getApplication());
        SpringApplicationUtils.setBasePackages(environment, packages);
    }

    private void setDefaultActiveProfile(ConfigurableEnvironment env) {
        String[] activeProfiles = env.getActiveProfiles();
        if (0 == activeProfiles.length) {
            env.setActiveProfiles(AbstractProfileEnvironmentPostProcessor.LOCAL);
        }
    }

    private void checkModules() {
        Set<String> missingModules = new HashSet<>();
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        for (Map.Entry<String, String> entry : STARTER_CLASS_ARTIFACT_ID_MAP.entrySet()) {
            String key = entry.getKey();
            if (!ClassUtils.isPresent(key, classLoader)) {
                missingModules.add(entry.getValue());
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : CONDITIONAL_CLASS_ARTIFACT_ID_MAP.entrySet()) {
            if (ClassUtils.isPresent(entry.getKey(), classLoader)) {
                Map<String, String> artifactIdMap = entry.getValue();
                for (Map.Entry<String, String> subEntry : artifactIdMap.entrySet()) {
                    if (!ClassUtils.isPresent(subEntry.getKey(), classLoader)) {
                        missingModules.add(subEntry.getValue());
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(missingModules)) {
            throw new MissingModuleException(missingModules);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}