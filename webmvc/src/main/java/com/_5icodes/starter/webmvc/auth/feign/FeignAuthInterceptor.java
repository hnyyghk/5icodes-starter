package com._5icodes.starter.webmvc.auth.feign;

import com._5icodes.starter.common.utils.IpUtils;
import com._5icodes.starter.webmvc.Region;
import com._5icodes.starter.webmvc.WebMvcConstants;
import com._5icodes.starter.webmvc.WebMvcProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignAuthInterceptor implements RequestInterceptor {
    private final WebMvcProperties webMvcProperties;

    public FeignAuthInterceptor(WebMvcProperties webMvcProperties) {
        this.webMvcProperties = webMvcProperties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(WebMvcConstants.GROUP_ID, webMvcProperties.getGroup());
        requestTemplate.header(WebMvcConstants.MODULE_ID, webMvcProperties.getModule());
        requestTemplate.header(WebMvcConstants.ZONE, Region.getZone());
        requestTemplate.header(WebMvcConstants.REQ_IP, IpUtils.getHostAddress());
    }
}