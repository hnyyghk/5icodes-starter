package com._5icodes.starter.demo.async;

import com._5icodes.starter.async.annotation.AsyncRun;
import com._5icodes.starter.async.annotation.Mode;
import com._5icodes.starter.async.callback.CallbackContext;
import com._5icodes.starter.async.policy.DelayTimeLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class HelloService {
    private final Set<String> successNames = new HashSet<>();

    @AsyncRun(maxRetry = 3, callbackName = "testCallback")
    public void sayHello(String name) {
        log.info("hello {}", name);
        if ("error".equals(name)) {
            throw new RuntimeException("test");
        }
    }

    @AsyncRun(maxRetry = 3, callbackName = "testCallback")
    public void sayHi(String name) {
        log.info("hi {}", name);
    }

    @AsyncRun(firstDelayTime = DelayTimeLevel.S_5, maxRetry = 3, mode = Mode.PROTECTION, callbackClass = TestCallback.class)
    public void protect(String name) {
        if (CallbackContext.get() != null && successNames.contains(name)) {
            log.info("{} is already success, protection not needed", name);
            successNames.remove(name);
            return;
        }
        int i = RandomUtils.nextInt();
        if (i % 2 == 0) {
            if (CallbackContext.get() == null) {
                successNames.add(name);
                log.info("protect run {} success by self", name);
            } else {
                log.info("protect run {} success by callback", name);
            }
        } else {
            throw new RuntimeException("protect");
        }
    }

    @AsyncRun(firstDelayTime = DelayTimeLevel.S_5, maxRetry = 3, mode = Mode.FAIL_RETRY, callbackClass = TestCallback.class)
    public void failRetry(String name) {
        int i = RandomUtils.nextInt();
        if (i % 2 == 0) {
            log.info("failRetry run {} success", name);
        } else {
            throw new RuntimeException("failRetry");
        }
    }
}