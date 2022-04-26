package com._5icodes.starter.stress.feign.test.local;

import cn.hutool.cache.impl.LRUCache;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import feign.Request;
import feign.Response;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * mock帮助类
 */
@UtilityClass
@Slf4j
public class MockHelper {
    /**
     * 最大缓存时间: 55秒
     */
    private final long TIMEOUT = 1000 * 55;
    /**
     * 缓存map
     */
    private final static LRUCache<String, Response> CACHE = new LRUCache<>(1000, TIMEOUT);
    /**
     * key max = 10K
     */
    private final Long MAX_KEY_SIZE = 10240L;

    /**
     * 根据请求获取处理类实例
     *
     * @param request 请求
     * @return com._5icodes.starter.stress.feign.test.local.MockMatch
     */
    private MockMatch getInstance(Request request) {
        String method = request.httpMethod().name();
        boolean support = StringUtils.containsAny(method, Request.HttpMethod.POST.name(), Request.HttpMethod.GET.name());
        if (!support) {
            throw new MockNotSupportException(String.format("request method %s not support", method));
        }
        if (Request.HttpMethod.GET.name().equalsIgnoreCase(method)) {
            return new GetMatch(request);
        }
        String contentType = getContentType(request);
        if (Request.HttpMethod.POST.name().equalsIgnoreCase(method) && contentType.contains(ContentType.APPLICATION_JSON.getMimeType())) {
            return new PostJsonMatch(request);
        }
        if (Request.HttpMethod.POST.name().equalsIgnoreCase(method) && contentType.contains(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
            return new PostEncoderMatch(request);
        }
        throw new MockNotSupportException(String.format("request method %s Content-Type %s not support", method, contentType));
    }

    /**
     * 获取请求头Content-Type
     *
     * @param request 请求
     * @return java.lang.String
     */
    private String getContentType(Request request) {
        Collection<String> headers = request.headers().get("Content-Type");
        Optional<String> optional = headers.stream().findAny();
        return optional.orElse("");
    }

    /**
     * 获取mock挡板请求体
     *
     * @param request 请求
     * @return feign.Response
     */
    public Response handle(Request request) {
        MockMatch mockMatch = getInstance(request);
        String key = cacheKey(request, mockMatch);
        if (CACHE.containsKey(key)) {
            TraceTestUtils.info("mock is success by cache {}", key);
            return CACHE.get(key);
        }
        MockData mockData = mockMatch.match();
        if (ObjectUtils.isEmpty(mockData)) {
            throw new MockNotSupportException("feign请求未匹配到mock资源");
        }
        Long timeout = mockData.getSleepTime();
        if (null != timeout && timeout > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                log.error("mock sleep error", e);
            }
        }
        Response response = Response.builder()
                .status(ObjectUtils.isEmpty(mockData.getResponseStatusCode()) ? 200 : mockData.getResponseStatusCode())
                .body(mockData.getResponseParams(), StandardCharsets.UTF_8)
                .request(request)
                .headers(request.headers())
                .build();
        if (StringUtils.isNotEmpty(key)) {
            CACHE.put(key, response);
        }
        return response;
    }

    private String cacheKey(Request request, MockMatch mockMatch) {
        long size = 0;
        List<String> args = mockMatch.getFeignArgs();
        List<String> headers = mockMatch.getFeignHeaders();
        args.addAll(headers);
        for (String arg : args) {
            size += arg.getBytes().length;
        }
        return size < MAX_KEY_SIZE ? String.format("%s=%s", request.url(), size) : "";
    }
}