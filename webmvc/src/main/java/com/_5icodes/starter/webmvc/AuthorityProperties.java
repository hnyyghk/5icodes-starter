package com._5icodes.starter.webmvc;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.exception.CodeMsgRegistry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Data
@ConfigurationProperties(prefix = WebMvcConstants.AUTHORITY_PREFIX)
public class AuthorityProperties implements CodeMsg {
    private Integer code = -105;
    private String message = "鉴权未通过";

    @PostConstruct
    public void postConstructMethod() {
        CodeMsgRegistry.register(this);
    }

    @PreDestroy
    public void preDestroyMethod() {
        CodeMsgRegistry.deRegister(this);
    }
}