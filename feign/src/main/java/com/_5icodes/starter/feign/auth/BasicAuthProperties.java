package com._5icodes.starter.feign.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feign.auth")
@Data
public class BasicAuthProperties {
    private String userName;
    private String password;
}