package com._5icodes.starter.demo;

import com._5icodes.starter.webmvc.feign.CodeMsgDecode;
import com._5icodes.starter.webmvc.result.ResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@CodeMsgDecode
@FeignClient(name = "baidu", url = "http://www.baidu.com")
public interface TestFeign {
    @GetMapping
    ResultDTO<String> getTest();
}