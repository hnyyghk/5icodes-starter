package com._5icodes.starter.demo.redisson;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"it", "local"})
public class RedissonDemoApplicationIT {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test(expected = QueryTimeoutException.class)
    public void testRedisTemplateTimeout() {
        String key = RandomStringUtils.randomAlphabetic(10);
        redisTemplate.opsForList().leftPop(key, 2, TimeUnit.SECONDS);
    }

    @Test
    public void testRedissonTimeout() throws InterruptedException {
        String key = RandomStringUtils.randomAlphabetic(10);
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(key);
        Object o = blockingDeque.pollFirst(2, TimeUnit.SECONDS);
        Assert.assertNull(o);
    }

    @Test
    public void testMapCache() {
        User expected = newRandomUser();
        String redisKey = RandomStringUtils.randomAlphabetic(5);
        RMapCache<String, User> testMapCache = redissonClient.getMapCache("testMapCache");
        testMapCache.put(redisKey, expected, 10, TimeUnit.SECONDS);
        User result = testMapCache.get(redisKey);
        MatcherAssert.assertThat(result, Matchers.instanceOf(User.class));
        MatcherAssert.assertThat(result, Matchers.samePropertyValuesAs(expected));
    }

    @Test
    public void testBucket() {
        User expected = newRandomUser();
        RBucket<User> testBucket = redissonClient.getBucket("testBucket");
        testBucket.set(expected, 10, TimeUnit.SECONDS);
        User result = testBucket.get();
        MatcherAssert.assertThat(result, Matchers.samePropertyValuesAs(expected));

        User newExpected = newRandomUser();
        boolean set = testBucket.trySet(newExpected, 10, TimeUnit.SECONDS);
        Assert.assertFalse(set);
        boolean compareAndSet = testBucket.compareAndSet(expected, newExpected);
        Assert.assertTrue(compareAndSet);
        User newResult = testBucket.get();
        MatcherAssert.assertThat(newResult, Matchers.samePropertyValuesAs(newExpected));
    }

    private User newRandomUser() {
        User user = new User();
        user.setId(RandomUtils.nextLong());
        user.setName(RandomStringUtils.randomAlphabetic(10));
        return user;
    }
}