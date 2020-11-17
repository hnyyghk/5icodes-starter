package com._5icodes.starter.web;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import com._5icodes.starter.common.utils.SpringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@Data
@ConfigurationProperties(prefix = WebConstants.SUCCESS_PREFIX)
public class SuccessProperties implements CodeMsg {
    private Integer code = 0;
    private String message = "操作成功";

    @PostConstruct
    public void postConstructMethod() {
        CodeMsgRegistry.register(SpringUtils.getBean(SuccessProperties.class));
    }
}