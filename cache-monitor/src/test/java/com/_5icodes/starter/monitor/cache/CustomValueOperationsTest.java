package com._5icodes.starter.monitor.cache;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class CustomValueOperationsTest {
    @Mock
    private ValueOperations<String, String> delegate;
    @InjectMocks
    private CustomValueOperations<String, String> customValueOperations;

    private final String key = RandomStringUtils.randomAlphabetic(10);

    private final Boolean boolRes = Boolean.FALSE;

    @Test
    public void testGet() {
        String res = RandomStringUtils.randomAlphabetic(10);
        Mockito.doReturn(res).when(delegate).get(key);
        Assert.assertEquals(res, customValueOperations.get(key));
    }

    @Test
    public void testGetAndSet() {
        String newValue = RandomStringUtils.randomAlphabetic(10);
        String oldValue = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(delegate.getAndSet(key, newValue)).thenReturn(oldValue);
        Assert.assertEquals(oldValue, customValueOperations.getAndSet(key, newValue));
    }

    @Test
    public void testIncrement() {
        Long res = RandomUtils.nextLong();
        Mockito.when(delegate.increment(key)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.increment(key));
    }

    @Test
    public void testIncrementLongDelta() {
        long delta = RandomUtils.nextLong();
        Long res = RandomUtils.nextLong();
        Mockito.when(delegate.increment(key, delta)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.increment(key, delta));
    }

    @Test
    public void testIncrementDoubleDelta() {
        double delta = RandomUtils.nextDouble();
        Double res = RandomUtils.nextDouble();
        Mockito.when(delegate.increment(key, delta)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.increment(key, delta));
    }

    @Test
    public void testDecrement() {
        Long res = RandomUtils.nextLong();
        Mockito.when(delegate.decrement(key)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.decrement(key));
    }

    @Test
    public void testDecrementDelta() {
        long delta = RandomUtils.nextLong();
        Long res = RandomUtils.nextLong();
        Mockito.when(delegate.decrement(key, delta)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.decrement(key, delta));
    }

    @Test
    public void testAppend() {
        String value = RandomStringUtils.randomAlphabetic(10);
        Integer res = RandomUtils.nextInt();
        Mockito.when(delegate.append(key, value)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.append(key, value));
    }

    @Test
    public void testGetRange() {
        long start = RandomUtils.nextLong();
        long end = RandomUtils.nextLong();
        String res = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(delegate.get(key, start, end)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.get(key, start, end));
    }

    @Test
    public void testMultiGet() {
        List<String> keys = Lists.newArrayList(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
        List<String> res = Lists.newArrayList(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
        Mockito.when(delegate.multiGet(keys)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.multiGet(keys));
    }

    @Test
    public void testMultiSet() {
        Map<String, String> map = new HashMap<>();
        customValueOperations.multiSet(map);
        Mockito.verify(delegate).multiSet(map);
    }

    @Test
    public void testMultiSetIfAbsent() {
        Map<String, String> map = new HashMap<>();
        Mockito.when(delegate.multiSetIfAbsent(map)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.multiSetIfAbsent(map));
        Mockito.verify(delegate).multiSetIfAbsent(map);
    }

    @Test
    public void testSet() {
        String value = RandomStringUtils.randomAlphabetic(10);
        customValueOperations.set(key, value);
        Mockito.verify(delegate).set(key, value);
    }

    @Test
    public void testSetOffset() {
        String value = RandomStringUtils.randomAlphabetic(10);
        long offset = RandomUtils.nextLong();
        customValueOperations.set(key, value, offset);
        Mockito.verify(delegate).set(key, value, offset);
    }

    @Test
    public void testSetIfAbsent() {
        String value = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(delegate.setIfAbsent(key, value)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.setIfAbsent(key, value));
        Mockito.verify(delegate).setIfAbsent(key, value);
    }

    @Test
    public void testSetIfAbsentTime() {
        String value = RandomStringUtils.randomAlphabetic(10);
        long time = RandomUtils.nextLong();
        Mockito.when(delegate.setIfAbsent(key, value, time, TimeUnit.MILLISECONDS)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.setIfAbsent(key, value, time, TimeUnit.MILLISECONDS));
        Mockito.verify(delegate).setIfAbsent(key, value, time, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSetIfPresent() {
        String value = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(delegate.setIfPresent(key, value)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.setIfAbsent(key, value));
        Mockito.verify(delegate).setIfPresent(key, value);
    }

    @Test
    public void testSetIfPresentTime() {
        String value = RandomStringUtils.randomAlphabetic(10);
        long time = RandomUtils.nextLong();
        Mockito.when(delegate.setIfPresent(key, value, time, TimeUnit.MILLISECONDS)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.setIfAbsent(key, value, time, TimeUnit.MILLISECONDS));
        Mockito.verify(delegate).setIfPresent(key, value, time, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSetTime() {
        String value = RandomStringUtils.randomAlphabetic(10);
        long time = RandomUtils.nextLong();
        customValueOperations.set(key, value, time, TimeUnit.MILLISECONDS);
        Mockito.verify(delegate).set(key, value, time, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSize() {
        Long res = RandomUtils.nextLong();
        Mockito.when(delegate.size(key)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.size(key));
    }

    @Test
    public void testSetBit() {
        long offset = RandomUtils.nextLong();
        Mockito.when(delegate.setBit(key, offset, true)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.setBit(key, offset, true));
        Mockito.verify(delegate).setBit(key, offset, true);
    }

    @Test
    public void testGetBit() {
        long offset = RandomUtils.nextLong();
        Mockito.when(delegate.getBit(key, offset)).thenReturn(boolRes);
        Assert.assertEquals(boolRes, customValueOperations.getBit(key, offset));
        Mockito.verify(delegate).getBit(key, offset);
    }

    @Test
    public void testBitField() {
        BitFieldSubCommands commands = Mockito.mock(BitFieldSubCommands.class);
        List<Long> res = Lists.newArrayList(RandomUtils.nextLong());
        Mockito.when(delegate.bitField(key, commands)).thenReturn(res);
        Assert.assertEquals(res, customValueOperations.bitField(key, commands));
    }

    @Test
    public void testGetOperations() {
        StringRedisTemplate mock = Mockito.mock(StringRedisTemplate.class);
        Mockito.when(delegate.getOperations()).thenReturn(mock);
        Assert.assertEquals(mock, customValueOperations.getOperations());
    }
}