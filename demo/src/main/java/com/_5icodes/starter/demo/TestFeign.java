package com._5icodes.starter.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "baidu",url = "http://www.baidu.com")
public interface TestFeign {
    @GetMapping
    String getTest();
}