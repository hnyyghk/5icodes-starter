package com._5icodes.starter.feign.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*"})
public class TimeoutKeyUtilsTest {
    private final Class<?> type = Object.class;

    private final Method method = ClassUtils.getMethod(Object.class, "toString");

    @Test
    public void testConnectTimeoutKey() {
        String connectTimeoutKey = TimeoutKeyUtils.connectTimeoutKey(type, method, "contextId");
        Assert.assertEquals("feign.client.config.contextId.toString().connectTimeout", connectTimeoutKey);
    }

    @Test
    public void testReadTimeoutKey() {
        String readTimeoutKey = TimeoutKeyUtils.readTimeoutKey(type, method, "contextId");
        Assert.assertEquals("feign.client.config.contextId.toString().readTimeout", readTimeoutKey);
    }
}