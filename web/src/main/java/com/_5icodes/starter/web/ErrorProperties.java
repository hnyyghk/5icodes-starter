package com._5icodes.starter.web;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import com._5icodes.starter.common.utils.SpringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@Data
@ConfigurationProperties(prefix = WebConstants.ERROR_PREFIX)
public class ErrorProperties implements CodeMsg {
    private Integer code = -1;
    private String message = "服务器竟然出错了";

    @PostConstruct
    public void postConstructMethod() {
        CodeMsgRegistry.register(SpringUtils.getBean(ErrorProperties.class));
    }
}