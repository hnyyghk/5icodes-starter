package com._5icodes.starter.webmvc;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Data
@ConfigurationProperties(prefix = WebMvcConstants.FLOW_PREFIX)
public class FlowProperties implements CodeMsg {
    private Integer code = -101;
    private String message = "请求太频繁了";

    @PostConstruct
    public void postConstructMethod() {
        CodeMsgRegistry.register(this);
    }

    @PreDestroy
    public void preDestroyMethod() {
        CodeMsgRegistry.deRegister(this);
    }
}