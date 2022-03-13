package com._5icodes.starter.redisson.lock;

import com._5icodes.starter.common.utils.SpringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
@Slf4j
public class RedisLockUtils {
    public final long DEFAULT_WAIT_TIME = 0;
    public final long DEFAULT_LEASE_TIME = -1;
    public final String DEFAULT_MESSAGE = "抱歉！服务正忙，请稍后重试";

    private <T, R> R rLockAndApply(long waitTime, long leaseTime, TimeUnit timeUnit, T t, Function<T, R> function, String message, boolean catchException, RLock rLock) {
        try {
            boolean locked;
            try {
                log.debug("tryLock");
                //加锁，等待waitTime，租约leaseTime
                locked = rLock.tryLock(waitTime, leaseTime, timeUnit);
            } catch (Exception e) {
                log.warn("tryLock failed", e);
                throw new LockFailedException(e);
            }
            if (!locked) {
                log.info("tryLock failed because of concurrence");
                throw new LockFailedException(message);
            }
            log.debug("tryLock success");
            return function.apply(t);
        } catch (LockFailedException e) {
            if (catchException) {
                return null;
            }
            throw e;
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                //解锁
                rLock.unlock();
            }
        }
    }

    public <T, R> R lockAndApply(String key, long waitTime, long leaseTime, TimeUnit timeUnit, T t, Function<T, R> function, String message, boolean catchException) {
        RedissonClient redissonClient = SpringUtils.getBean(RedissonClient.class);
        RLock rLock = redissonClient.getLock(key);
        return rLockAndApply(waitTime, leaseTime, timeUnit, t, function, message, catchException, rLock);
    }

    public <T, R> R lockAndApply(String key, T t, Function<T, R> function) {
        return lockAndApply(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS, t, function, DEFAULT_MESSAGE, false);
    }

    public <T> void lockAndAccept(String key, long waitTime, long leaseTime, TimeUnit timeUnit, T t, Consumer<T> consumer, String message, boolean catchException) {
        lockAndApply(key, waitTime, leaseTime, timeUnit, t, c -> {
            consumer.accept(c);
            return null;
        }, message, catchException);
    }

    public <T> void lockAndAccept(String key, T t, Consumer<T> consumer) {
        lockAndAccept(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS, t, consumer, DEFAULT_MESSAGE, false);
    }

    public <T, R> R lockAndApply(List<String> keyList, long waitTime, long leaseTime, TimeUnit timeUnit, T t, Function<T, R> function, String message, boolean catchException) {
        RedissonClient redissonClient = SpringUtils.getBean(RedissonClient.class);
        RedissonMultiLock rLock = new RedissonMultiLock(keyList.stream().map(redissonClient::getLock).toArray(RLock[]::new));
        return rLockAndApply(waitTime, leaseTime, timeUnit, t, function, message, catchException, rLock);
    }

    public <T, R> R lockAndApply(List<String> keyList, T t, Function<T, R> function) {
        return lockAndApply(keyList, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS, t, function, DEFAULT_MESSAGE, false);
    }

    public <T> void lockAndAccept(List<String> keyList, long waitTime, long leaseTime, TimeUnit timeUnit, T t, Consumer<T> consumer, String message, boolean catchException) {
        lockAndApply(keyList, waitTime, leaseTime, timeUnit, t, c -> {
            consumer.accept(c);
            return null;
        }, message, catchException);
    }

    public <T> void lockAndAccept(List<String> keyList, T t, Consumer<T> consumer) {
        lockAndAccept(keyList, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS, t, consumer, DEFAULT_MESSAGE, false);
    }
}