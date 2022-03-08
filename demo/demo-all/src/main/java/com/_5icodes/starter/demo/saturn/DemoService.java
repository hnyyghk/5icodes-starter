package com._5icodes.starter.demo.saturn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DemoService {
    public void doing() {
        log.info("DemoService is doing...");
    }
}