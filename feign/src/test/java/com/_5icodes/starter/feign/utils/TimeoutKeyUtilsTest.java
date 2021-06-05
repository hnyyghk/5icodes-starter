package com._5icodes.starter.feign.utils;

import feign.Feign;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*"})
@PrepareForTest(Feign.class)
public class TimeoutKeyUtilsTest {
    private Class type = Object.class;

    private Method method = ClassUtils.getMethod(Object.class, "toString");

    @Before
    public void before() {
        PowerMockito.mockStatic(Feign.class);
        PowerMockito.when(Feign.configKey(type, method)).thenReturn("val");
    }

    @Test
    public void testConnectTimeoutKey() {
        String connectTimeoutKey = TimeoutKeyUtils.connectTimeoutKey(type, method);
        Assert.assertEquals("feign.val.ConnectTimeout", connectTimeoutKey);
    }

    @Test
    public void testReadTimeoutKey() {
        String readTimeoutKey = TimeoutKeyUtils.readTimeoutKey(type, method);
        Assert.assertEquals("feign.val.ReadTimeout", readTimeoutKey);
    }
}