package com._5icodes.starter.feign;

import com._5icodes.starter.feign.auth.BasicAuthProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.support.StandardServletEnvironment;

public class AuthFeignEnvInjectorTest {
    private final FeignEnvInjector feignEnvInjector = new FeignEnvInjector();
    private ConfigurableEnvironment environment;
    private final SpringApplication application = Mockito.mock(SpringApplication.class);

    @Before
    public void init() {
        environment = new StandardServletEnvironment();
    }

    @Test
    public void onDev() {
        feignEnvInjector.onDev(environment, application);
        BasicAuthProperties basicAuthProperties = bind(environment);
        Assert.assertEquals("testPassword", basicAuthProperties.getPassword());
    }

    @Test
    public void onAllProfiles() {
        feignEnvInjector.onAllProfiles(environment, application);
        BasicAuthProperties basicAuthProperties = bind(environment);
        Assert.assertEquals("testUserName", basicAuthProperties.getUserName());
    }

    private BasicAuthProperties bind(ConfigurableEnvironment environment) {
        Binder binder = Binder.get(environment);
        BindResult<BasicAuthProperties> bindResult = binder.bind("feign.auth", Bindable.of(BasicAuthProperties.class));
        Assert.assertTrue(bindResult.isBound());
        return bindResult.get();
    }
}