package com._5icodes.starter.webmvc.properties;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import com._5icodes.starter.webmvc.WebMvcConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Data
@ConfigurationProperties(prefix = WebMvcConstants.ERROR_PREFIX)
public class ErrorProperties implements CodeMsg {
    private Integer code = -100;
    private String message = "服务器竟然出错了";

    @PostConstruct
    public void postConstructMethod() {
        CodeMsgRegistry.register(this);
    }

    @PreDestroy
    public void preDestroyMethod() {
        CodeMsgRegistry.deRegister(this);
    }
}