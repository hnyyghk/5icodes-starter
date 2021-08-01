package com._5icodes.starter.webmvc.auth;

import com._5icodes.starter.webmvc.WebMvcConstants;
import com._5icodes.starter.webmvc.WebMvcProperties;
import com._5icodes.starter.webmvc.common.OnlyOnceHandlerInterceptor;
import com._5icodes.starter.webmvc.common.OnlyOnceInterceptorConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Slf4j
public class PermissionInterceptor implements OnlyOnceInterceptorConfigurer, Ordered {
    @Autowired
    private WebMvcProperties webMvcProperties;

    @Override
    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String groupId = request.getHeader(WebMvcConstants.GROUP_ID);
        String moduleId = request.getHeader(WebMvcConstants.MODULE_ID);
        if (moduleId == null || groupId == null) {
            //todo
            log.warn("unauthorized request without groupId or moduleId from server: {}", request.getRemoteAddr());
            return true;
        }
        if (groupId.equals(webMvcProperties.getGroup())) {
            return true;
        }
        Set<String> allowList = webMvcProperties.getAllowList();
        if (CollectionUtils.isEmpty(allowList) || !allowList.contains(moduleId)) {
            log.warn("unauthorized request from module: {}", moduleId);
        }
        return true;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}