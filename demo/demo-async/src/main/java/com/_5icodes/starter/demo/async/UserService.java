package com._5icodes.starter.demo.async;

import com._5icodes.starter.async.annotation.AsyncRun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {
    @AsyncRun(orderly = true, id = "#p0.id")
    public void sayHelloOrderly(User user, String word) throws Exception {
        log.info("hello orderly {} : {}", user, word);
        TimeUnit.SECONDS.sleep(3);
        log.info("finish");
    }
}