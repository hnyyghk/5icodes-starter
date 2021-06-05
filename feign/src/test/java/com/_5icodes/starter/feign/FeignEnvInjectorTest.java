package com._5icodes.starter.feign;

import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.feign.custom.FeignClientCustom;
import com._5icodes.starter.feign.utils.FeignPropertyUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*"})
@PrepareForTest({PropertySourceUtils.class, AnnotationUtils.class, FeignPropertyUtils.class})
public class FeignEnvInjectorTest {
    @Mock
    private FeignEnvInjector feignEnvInjector;

    @Test
    public void testOnAllProfiles() {
        ConfigurableEnvironment environment = PowerMockito.mock(ConfigurableEnvironment.class);
        SpringApplication application = PowerMockito.mock(SpringApplication.class);
        PowerMockito.mockStatic(PropertySourceUtils.class);
        PropertySourceUtils.put(environment, "ribbon.ConnectTimeout", FeignConstants.DEFAULT_CONNECT_TIMEOUT);
        PropertySourceUtils.put(environment, "ribbon.ReadTimeout", FeignConstants.DEFAULT_READ_TIMEOUT);
        Class<?> mainApplicationClass = application.getMainApplicationClass();
        PowerMockito.mockStatic(AnnotationUtils.class);
        FeignClientCustom feignClientCustom = AnnotationUtils.findAnnotation(mainApplicationClass, FeignClientCustom.class);
        if (null != feignClientCustom) {
            FeignPropertyUtils.process(feignClientCustom, environment, "", FeignClientCustom.class);
        }
        feignEnvInjector.onAllProfiles(environment, application);
    }
}