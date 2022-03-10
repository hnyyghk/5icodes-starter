package com._5icodes.starter.demo.redisson;

import com._5icodes.starter.common.utils.ExceptionUtils;
import com._5icodes.starter.redisson.lock.LockFailedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"it", "local"})
public class LockDemoApplicationIT {
    @Autowired
    private LockTestService lockTestService;

    private static final int maxConcurrence = 10;
    private final ExecutorService executorService = Executors.newFixedThreadPool(maxConcurrence);

    @Test
    public void testLockOneByOne() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        CyclicBarrier barrier = new CyclicBarrier(maxConcurrence, () -> {
            int cur = count.incrementAndGet();
            if (cur == 2) {
                latch.countDown();
            }
        });
        lockTestService.reset();
        for (int i = 0; i < maxConcurrence; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    lockTestService.executeOneByOne();
                    barrier.await();
                } catch (Exception e) {
                    throw new AssertionError("execute test error", e);
                }
            });
        }
        latch.await(2L, TimeUnit.SECONDS);
        long sum = lockTestService.getSum();
        Assert.assertEquals(1, sum);
        int executed = lockTestService.getExecuted().intValue();
        Assert.assertEquals(maxConcurrence, executed);
    }

    @Test
    public void testLockFailReturnNull() throws InterruptedException {
        String nullVal = RandomStringUtils.randomAlphabetic(5);
        CyclicBarrier barrier = new CyclicBarrier(2);
        BlockingDeque<String> deque = new LinkedBlockingDeque<>(2);
        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    String result = lockTestService.sayHi();
                    if (result == null) {
                        deque.add(nullVal);
                    } else {
                        deque.add(result);
                    }
                } catch (Exception e) {
                    throw new AssertionError("execute test error", e);
                }
            });
        }
        LinkedList<String> list = new LinkedList<>();
        list.add(deque.poll(1L, TimeUnit.SECONDS));
        list.add(deque.poll(1L, TimeUnit.SECONDS));
        MatcherAssert.assertThat(list, Matchers.containsInAnyOrder(nullVal, LockTestService.HI));
    }

    @Test
    public void testLockFailThrowException() throws InterruptedException {
        String expVal = RandomStringUtils.randomAlphabetic(5);
        CyclicBarrier barrier = new CyclicBarrier(2);
        BlockingDeque<String> deque = new LinkedBlockingDeque<>(2);
        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    String result = lockTestService.sayHiMayFail();
                    deque.add(result);
                } catch (Exception e) {
                    Throwable throwable = ExceptionUtils.getRealException(e);
                    if (throwable instanceof LockFailedException) {
                        deque.add(expVal);
                    } else {
                        throw new AssertionError("execute test error", e);
                    }
                }
            });
        }
        LinkedList<String> list = new LinkedList<>();
        list.add(deque.poll(1L, TimeUnit.SECONDS));
        list.add(deque.poll(1L, TimeUnit.SECONDS));
        MatcherAssert.assertThat(list, Matchers.containsInAnyOrder(expVal, LockTestService.HI));
    }
}