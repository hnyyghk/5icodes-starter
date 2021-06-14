package com._5icodes.starter.stress.feign.test.local;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * mock服务
 */
@FeignClient(name = "mock", url = "${mock.server}", path = "${mock.path}")
public interface MockSao {
    /**
     * 获取mock api结果集
     *
     * @param request 请求参数
     * @return 结果
     */
    @PostMapping("/api/queryMockListByCall")
    Map<String, String> queryMockListByCall(@RequestBody Map<String, String> request);
}