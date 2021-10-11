package com._5icodes.starter.webmvc.auth.feign;

import com._5icodes.starter.common.utils.HostNameUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.sleuth.SleuthConstants;
import com._5icodes.starter.web.WebConstants;
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
        requestTemplate.header(WebConstants.GROUP_ID, webMvcProperties.getGroup());
        requestTemplate.header(WebConstants.MODULE_ID, webMvcProperties.getModule());
        requestTemplate.header(WebConstants.ZONE, RegionUtils.getZone());
        requestTemplate.header(SleuthConstants.REQ_IP, HostNameUtils.getHostAddress());
    }
}