package com._5icodes.starter.common.application;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import lombok.Getter;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

@Getter
public class ApplicationRunListenerAdapter implements SpringApplicationRunListener {
    private final SpringApplication application;
    private final String[] args;

    public ApplicationRunListenerAdapter(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doStarting(bootstrapContext);
        }
        SpringApplicationRunListener.super.starting(bootstrapContext);
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doEnvironmentPrepared(bootstrapContext, environment);
        }
        SpringApplicationRunListener.super.environmentPrepared(bootstrapContext, environment);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doContextPrepared(context);
        }
        SpringApplicationRunListener.super.contextPrepared(context);
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doContextLoaded(context);
        }
        SpringApplicationRunListener.super.contextLoaded(context);
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doStarted(context, timeTaken);
        }
        SpringApplicationRunListener.super.started(context, timeTaken);
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doReady(context, timeTaken);
        }
        SpringApplicationRunListener.super.ready(context, timeTaken);
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        if (SpringApplicationUtils.isBootApplication(application)) {
            doFailed(context, exception);
        }
        SpringApplicationRunListener.super.failed(context, exception);
    }

    protected void doStarting(ConfigurableBootstrapContext bootstrapContext) {
    }

    protected void doEnvironmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
    }

    protected void doContextPrepared(ConfigurableApplicationContext context) {
    }

    protected void doContextLoaded(ConfigurableApplicationContext context) {
    }

    protected void doStarted(ConfigurableApplicationContext context, Duration timeTaken) {
    }

    protected void doReady(ConfigurableApplicationContext context, Duration timeTaken) {
    }

    protected void doFailed(ConfigurableApplicationContext context, Throwable exception) {
    }
}