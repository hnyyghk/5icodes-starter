package com._5icodes.starter.common.application;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Getter
public class ApplicationRunListenerAdapter implements SpringApplicationRunListener {
    private final SpringApplication application;
    private final String[] args;

    public ApplicationRunListenerAdapter(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doStarting();
        }
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doEnvironmentPrepared(environment);
        }
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doContextPrepared(context);
        }
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doContextLoaded(context);
        }
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doStarted(context);
        }
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doRunning(context);
        }
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doFailed(context, exception);
        }
    }

    protected void doStarting() {
    }

    protected void doEnvironmentPrepared(ConfigurableEnvironment environment) {
    }

    protected void doContextPrepared(ConfigurableApplicationContext context) {
    }

    protected void doContextLoaded(ConfigurableApplicationContext context) {
    }

    protected void doStarted(ConfigurableApplicationContext context) {
    }

    protected void doRunning(ConfigurableApplicationContext context) {
    }

    protected void doFailed(ConfigurableApplicationContext context, Throwable exception) {
    }
}