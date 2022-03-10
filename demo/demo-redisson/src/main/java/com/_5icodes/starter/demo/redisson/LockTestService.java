package com._5icodes.starter.demo.redisson;

import com._5icodes.starter.redisson.lock.Locked;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LockTestService {
    public static final String HI = RandomStringUtils.randomAlphabetic(10);
    private final AtomicInteger concurrence = new AtomicInteger(0);
    @Getter
    private final AtomicInteger executed = new AtomicInteger(0);
    @Getter
    private volatile long sum = 0;

    public void reset() {
        concurrence.set(0);
        executed.set(0);
        sum = 0;
    }

    @Locked
    public String sayHi() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100L);
        return HI;
    }

    @Locked(catchException = false)
    public String sayHiMayFail() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100L);
        return HI;
    }

    @Locked(waitTime = 1500)
    public void executeOneByOne() throws InterruptedException {
        executed.incrementAndGet();
        int temp = concurrence.incrementAndGet();
        if (temp > sum) {
            sum = temp;
        }
        TimeUnit.MILLISECONDS.sleep(100L);
        concurrence.decrementAndGet();
    }
}