package com._5icodes.starter.feign;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "feign.httpclient")
public class CustomFeignHttpClientProperties extends FeignHttpClientProperties {
    private int connectionRequestTimeout = FeignConstants.DEFAULT_CONNECTION_REQUEST_TIMEOUT;
}