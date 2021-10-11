package com._5icodes.starter.webflux.monitor;

import com._5icodes.starter.common.utils.HostNameUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.web.WebConstants;
import com._5icodes.starter.web.WebProperties;
import com._5icodes.starter.web.monitor.FillSleuthPropagationAccessLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WebFluxAccessLog extends FillSleuthPropagationAccessLog implements WebFilter, Ordered {
    private static final String GATEWAY_NAME = "gatewayName";
    private static final String GATEWAY_GROUP_NAME = "gatewayGroupName";
    private static final String WEB_URI = "webUri";

    private final WebProperties webProperties;

    public WebFluxAccessLog(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getAttributes().put(WebConstants.ACCESS_LOG_START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            try {
                long endTime = System.currentTimeMillis();
                long startTime = exchange.getAttribute(WebConstants.ACCESS_LOG_START_TIME);
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                Map<String, Object> accessLog = new HashMap<>();

                accessLog.put("version", webProperties.getAccessVersion());
                accessLog.put("caller", getCaller(request));
                accessLog.put("callee", getCallee(request));

                String webUri = exchange.getAttribute(WEB_URI);
                if (webUri != null) {
                    accessLog.put("resource", webUri);
                } else {
                    accessLog.put("resource", request.getPath().toString());
                }
                accessLog.put("method", request.getMethod().name());
                String referer = request.getHeaders().getFirst(HttpHeaders.REFERER);
                accessLog.put("referer", referer == null ? "" : referer);
                accessLog.put("startTime", startTime);
                long reqLength = request.getHeaders().getContentLength();
                accessLog.put("reqLength", reqLength < 0 ? 0 : reqLength);
                String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
                accessLog.put("userAgent", userAgent == null ? "" : userAgent);

                HttpStatus status = response.getStatusCode();
                accessLog.put("status", status == null ? 480 : status.value());
                long resLength = response.getHeaders().getContentLength();
                accessLog.put("resLength", resLength < 0 ? 0 : resLength);
                accessLog.put("rt", endTime - startTime);
                accessLog.put("endTime", endTime);

                Map<String, Object> attribute = exchange.getAttribute(WebConstants.ACCESS_LOG_EXTEND_KEY);
                if (null != attribute) {
                    accessLog.putAll(attribute);
                }
                sendAccessLog(accessLog);
            } catch (Exception e) {
                log.error("WebFluxAccessLog filter error:", e);
            }
        }));
    }

    /**
     * 被调日志上报
     *
     * @param request
     * @return
     */
    private Map<String, Object> getCallee(ServerHttpRequest request) {
        Map<String, Object> callee = new HashMap<>();
        callee.put("moduleId", SpringApplicationUtils.getApplicationName());
        String groupId = request.getHeaders().getFirst(WebConstants.GROUP_ID);
        callee.put("groupId", groupId == null ? "" : groupId);
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
    private Map<String, Object> getCaller(ServerHttpRequest request) {
        Map<String, Object> caller = new HashMap<>();
        caller.put("moduleId", GATEWAY_NAME);
        String groupId = request.getHeaders().getFirst(GATEWAY_GROUP_NAME);
        caller.put("groupId", groupId == null ? "" : groupId);
        caller.put("zone", RegionUtils.getZone());
        String reqIp = request.getRemoteAddress() == null ? "" : request.getRemoteAddress().getAddress().getHostAddress();
        caller.put("reqIp", reqIp);
        return caller;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}