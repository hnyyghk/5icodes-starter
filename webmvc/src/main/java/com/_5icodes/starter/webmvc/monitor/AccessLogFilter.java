package com._5icodes.starter.webmvc.monitor;

import brave.propagation.CurrentTraceContext;
import com._5icodes.starter.common.CommonConstants;
import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.common.utils.HostNameUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.web.WebProperties;
import com._5icodes.starter.web.monitor.FillSleuthPropagationAccessLog;
import com._5icodes.starter.webmvc.WebMvcConstants;
import com._5icodes.starter.webmvc.common.OnlyOnceInterceptorConfigurer;
import com._5icodes.starter.webmvc.common.RequestMappingRegister;
import lombok.extern.slf4j.Slf4j;
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
    private final RequestMappingRegister register;
    private final WebProperties webProperties;

    public AccessLogFilter(CurrentTraceContext currentTraceContext, RequestMappingRegister register, WebProperties webProperties) {
        super(currentTraceContext);
        this.register = register;
        this.webProperties = webProperties;
    }

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
        accessLog.put("caller", getCaller(request));
        accessLog.put("callee", getCallee());

        accessLog.put("resource", resource);
        accessLog.put("method", request.getMethod());
        String referer = request.getHeader(HttpHeaders.REFERER);
        accessLog.put("referer", referer == null ? "" : referer);
        accessLog.put("startTime", startTime);
        String reqLength = request.getHeader(HttpHeaders.CONTENT_LENGTH);
        accessLog.put("reqLength", reqLength == null ? 0 : Long.parseLong(reqLength));
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        accessLog.put("userAgent", userAgent == null ? "" : userAgent);

        accessLog.put("status", response.getStatus());
        String resLength = response.getHeader(HttpHeaders.CONTENT_LENGTH);
        accessLog.put("resLength", resLength == null ? 0 : Long.parseLong(resLength));
        accessLog.put("rt", endTime - startTime);
        accessLog.put("endTime", endTime);

        Object resultCode = request.getAttribute(WebMvcConstants.RESULT_CODE);
        if (resultCode instanceof Integer) {
            accessLog.put("resultCode", resultCode);
        }
        if (RequestContextHolder.getRequestAttributes() != null) {
            Map<String, Object> attribute = (Map<String, Object>) request.getAttribute(WebConstants.ACCESS_LOG_EXTEND_KEY);
            if (null != attribute) {
                accessLog.putAll(attribute);
            }
        }
        sendAccessLog(accessLog);
    }

    /**
     * 被调日志上报
     *
     * @return
     */
    private Map<String, Object> getCallee() {
        Map<String, Object> callee = new HashMap<>();
        callee.put("moduleId", SpringApplicationUtils.getApplicationName());
        callee.put(CommonConstants.APP_GROUP, GrayUtils.isAppGroup() ? GrayUtils.getAppGroup() : "");
        callee.put("zone", RegionUtils.getZone());
        callee.put("reqIp", HostNameUtils.getHostAddress());
        return callee;
    }

    /**
     * 获取主调
     *
     * @param request
     * @return
     */
    private Map<String, Object> getCaller(HttpServletRequest request) {
        Map<String, Object> caller = new HashMap<>();
        caller.put("moduleId", request.getHeader(WebConstants.MODULE_ID));
        caller.put("groupId", request.getHeader(WebConstants.GROUP_ID));
        caller.put("zone", request.getHeader(WebConstants.ZONE));
        String reqIp = request.getHeader(WebConstants.REQ_IP);
        caller.put("reqIp", reqIp != null ? reqIp : request.getRemoteAddr());
        return caller;
    }

    @Override
    public int getOrder() {
        return -3;
    }
}