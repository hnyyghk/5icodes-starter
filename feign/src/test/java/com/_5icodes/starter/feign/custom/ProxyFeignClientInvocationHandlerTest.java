package com._5icodes.starter.feign.custom;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyFeignClientInvocationHandlerTest {
    @Test
    public void testPattern() {
        Pattern pattern = Pattern.compile("feign\\.([^.]*).(connect|read)Timeout");
        Matcher matcher = pattern.matcher("feign.class#method.connectTimeout");
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("class#method", matcher.group(1));
        matcher = pattern.matcher("feign.class#method(,).readTimeout");
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("class#method(,)", matcher.group(1));

        matcher = pattern.matcher("feign1.claa#method.connectTimeout");
        Assert.assertFalse(matcher.matches());
        matcher = pattern.matcher("feign.any#any.1connectTimeout");
        Assert.assertFalse(matcher.matches());
    }
}