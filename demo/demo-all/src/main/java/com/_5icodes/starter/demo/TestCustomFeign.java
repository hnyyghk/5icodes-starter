package com._5icodes.starter.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "testCustomFeign", url = "http://localhost:${server.port:8080}", path = "${server.servlet.context-path:}")
public interface TestCustomFeign {
    @PostMapping("/testCustomFeign")
    void testCustomFeign(@RequestBody Map<String, String> map);
}