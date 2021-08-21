package com._5icodes.starter.webmvc.monitor;

import com._5icodes.starter.sleuth.SleuthConstants;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.web.WebProperties;
import com._5icodes.starter.web.log.FillSleuthPropagationAccessLog;
import com._5icodes.starter.webmvc.WebMvcConstants;
import com._5icodes.starter.webmvc.common.OnlyOnceInterceptorConfigurer;
import com._5icodes.starter.webmvc.common.RequestMappingRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AccessLogFilter extends FillSleuthPropagationAccessLog implements OnlyOnceInterceptorConfigurer, Ordered {
    @Autowired
    private RequestMappingRegister register;
    @Autowired
    private WebProperties webProperties;

    @Override
    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(WebConstants.ACCESS_LOG_START_TIME, startTime);
        return true;
    }

    @Override
    public void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (long) request.getAttribute(WebConstants.ACCESS_LOG_START_TIME);
        String resource = request.getRequestURI();
        if (handler instanceof HandlerMethod) {
            resource = register.getSentinelKey((HandlerMethod) handler);
        }
        long endTime = System.currentTimeMillis();
        Map<String, Object> accessLog = new HashMap<>();
        accessLog.put("version", webProperties.getAccessVersion());
        accessLog.put("caller", getCallerMap(request));
        accessLog.put("resource", resource);
        accessLog.put("method", request.getMethod());
        String refererHeader = request.getHeader(HttpHeaders.REFERER);
        accessLog.put("referer", refererHeader == null ? "" : refererHeader);
        accessLog.put("startTime", startTime);
        String reqLengthHeader = request.getHeader(HttpHeaders.CONTENT_LENGTH);
        Integer reqLength = reqLengthHeader == null ? 0 : Integer.parseInt(reqLengthHeader);
        accessLog.put("reqLength", reqLength);
        String userAgentHeader = request.getHeader(HttpHeaders.USER_AGENT);
        accessLog.put("userAgent", userAgentHeader == null ? "" : userAgentHeader);

        accessLog.put("status", response.getStatus());
        String resLengthHeader = response.getHeader(HttpHeaders.CONTENT_LENGTH);
        Integer resLength = resLengthHeader == null ? 0 : Integer.parseInt(resLengthHeader);
        accessLog.put("resLength", resLength);
        accessLog.put("rt", endTime - startTime);
        accessLog.put("endTime", endTime);

        Object resultCode = request.getAttribute(WebMvcConstants.RESULT_CODE);
        if (resultCode instanceof Integer) {
            accessLog.put("resultCode", resultCode);
        }
        if (RequestContextHolder.getRequestAttributes() != null) {
            Map attribute = (Map) request.getAttribute(WebConstants.ACCESS_LOG_EXTEND_KEY);
            if (null != attribute) {
                accessLog.putAll(attribute);
            }
        }
        sendAccessLog(accessLog);
    }

    private Map<String, Object> getCallerMap(HttpServletRequest request) {
        Map<String, Object> caller = new HashMap<>();
        caller.put("moduleId", request.getHeader(WebConstants.MODULE_ID));
        caller.put("groupId", request.getHeader(WebConstants.GROUP_ID));
        caller.put("zone", request.getHeader(WebConstants.ZONE));
        String reqIp = request.getHeader(SleuthConstants.REQ_IP);
        caller.put("reqIp", reqIp != null ? reqIp : request.getRemoteAddr());
        return caller;
    }

    @Override
    public int getOrder() {
        return -3;
    }
}