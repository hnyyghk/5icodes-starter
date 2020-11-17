package com._5icodes.starter.common;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

public class AbstractProfileEnvironmentPostProcessor implements EnvironmentPostProcessor {
    public static final String LOCAL = "local";
    public static final String DEV = "dev";
    public static final String STG = "stg";
    public static final String UAT = "uat";
    public static final String GRAY = "gray";
    public static final String PRD = "prd";
    public static final String IT = "it";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        if (!shouldProcess(env, application)) {
            return;
        }
        String[] activeProfiles = env.getActiveProfiles();
        List<String> profileList = Arrays.asList(activeProfiles);
        if (profileList.contains(PRD)) {
            onPrd(env, application);
        } else if (profileList.contains(GRAY)) {
            onGray(env, application);
        } else if (profileList.contains(UAT)) {
            onUat(env, application);
        } else if (profileList.contains(STG)) {
            onStg(env, application);
        } else if (profileList.contains(DEV)) {
            onDev(env, application);
        } else if (profileList.contains(LOCAL)) {
            onLocal(env, application);
        }
        if (profileList.contains(IT)) {
            onIntegrationTest(env, application);
        }
        onAllProfiles(env, application);
    }

    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
    }

    protected void onIntegrationTest(ConfigurableEnvironment env, SpringApplication application) {
    }

    protected void onLocal(ConfigurableEnvironment env, SpringApplication application) {
        onDev(env, application);
    }

    protected void onDev(ConfigurableEnvironment env, SpringApplication application) {
    }

    protected void onStg(ConfigurableEnvironment env, SpringApplication application) {
    }

    protected void onUat(ConfigurableEnvironment env, SpringApplication application) {
        onStg(env, application);
    }

    protected void onGray(ConfigurableEnvironment env, SpringApplication application) {
        onPrd(env, application);
    }

    protected void onPrd(ConfigurableEnvironment env, SpringApplication application) {
    }

    protected boolean shouldProcess(ConfigurableEnvironment env, SpringApplication application) {
        return SpringApplicationUtils.isBootApplication(application);
    }
}