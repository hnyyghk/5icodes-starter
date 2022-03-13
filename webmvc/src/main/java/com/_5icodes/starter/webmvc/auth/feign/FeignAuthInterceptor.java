package com._5icodes.starter.webmvc.auth.feign;

import com._5icodes.starter.common.utils.HostNameUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.sleuth.SleuthConstants;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.webmvc.WebMvcProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class FeignAuthInterceptor implements RequestInterceptor {
    private final WebMvcProperties webMvcProperties;

    public FeignAuthInterceptor(WebMvcProperties webMvcProperties) {
        this.webMvcProperties = webMvcProperties;
    }

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        template.header(WebConstants.PRE_MODULE_ID, request.getHeader(WebConstants.MODULE_ID));
        template.header(WebConstants.PRE_REQ_URI, request.getRequestURI());
        template.header(WebConstants.GROUP_ID, webMvcProperties.getGroup());
        template.header(WebConstants.MODULE_ID, webMvcProperties.getModule());
        template.header(WebConstants.ZONE, RegionUtils.getZone());
        template.header(WebConstants.REQ_IP, HostNameUtils.getHostAddress());
    }
}