package com._5icodes.starter.webmvc;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Data
@ConfigurationProperties(prefix = WebMvcConstants.SYSTEM_BLOCK_PREFIX)
public class SystemBlockProperties implements CodeMsg {
    private Integer code = -104;
    private String message = "关联系统繁忙";

    @PostConstruct
    public void postConstructMethod() {
        CodeMsgRegistry.register(this);
    }

    @PreDestroy
    public void preDestroyMethod() {
        CodeMsgRegistry.deRegister(this);
    }
}