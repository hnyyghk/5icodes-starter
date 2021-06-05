package com._5icodes.starter.feign.custom;

import feign.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.NamedThreadLocal;

import java.util.concurrent.TimeUnit;

public class FeignRequestOptionsContextTest {
    @Mock
    private ThreadLocal<Request.Options> threadLocal;

    @Before
    public void setUp() {
        threadLocal = new NamedThreadLocal<>("feignCustom");
    }

    @Test
    public void testSetAndGet() {
        Request.Options options = new Request.Options(100, TimeUnit.MILLISECONDS, 200, TimeUnit.MILLISECONDS, true);
        threadLocal.set(options);
        Assert.assertEquals(100, threadLocal.get().connectTimeoutMillis());
    }
}