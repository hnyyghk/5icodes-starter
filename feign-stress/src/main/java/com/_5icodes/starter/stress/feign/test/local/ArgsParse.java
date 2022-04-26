package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.stress.feign.FeignStressConstants;
import com._5icodes.starter.web.WebConstants;
import feign.Request;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@UtilityClass
public class ArgsParse {
    /**
     * 模板key
     */
    private final String TEMPLATE_KEY = "%s=%s";
    /**
     * trace header list
     */
    private final List<String> TRACE_HEADER_LIST = Arrays.asList("X-B3-SPANID", "X-B3-PARENTSPANID", "X-B3-SAMPLED", "X-B3-TRACEID", WebConstants.MODULE_ID, "ACCEPT");

    public List<String> parseHeader(String header) {
        header = header.trim();
        List<String> list = new ArrayList<>();
        boolean isJson = header.startsWith("{");
        if (isJson) {
            Map<String, String> headerJson = JsonUtils.parseToMap(header, String.class, String.class);
            for (String key : headerJson.keySet()) {
                list.add(String.format(TEMPLATE_KEY, key.toUpperCase(), headerJson.get(key)));
            }
            return list;
        }
        String[] headers = header.split("\n");
        Arrays.stream(headers).forEach(h -> {
            String[] kv = h.split(":");
            if (!ArrayUtils.isEmpty(kv) && kv.length > 1) {
                list.add(String.format(TEMPLATE_KEY, kv[0].toUpperCase(), kv[1]));
            }
        });
        return list;
    }

    public List<String> parseQueryArgs(String query) {
        query = query.trim();
        if (StringUtils.isBlank(query)) {
            return new ArrayList<>();
        }
        String[] str = query.split("&");
        return Arrays.asList(str);
    }

    public List<String> parseJsonArgs(String requestBody) {
        requestBody = requestBody.trim();
        if (StringUtils.isBlank(requestBody)) {
            return new ArrayList<>();
        }
        Map<String, String> requestBodyJson = JsonUtils.parseToMap(requestBody, String.class, String.class);
        List<String> list = new ArrayList<>();
        for (String key : requestBodyJson.keySet()) {
            String value = requestBodyJson.get(key);
            if (StringUtils.isNotBlank(value)) {
                list.add(String.format(TEMPLATE_KEY, key, value.replaceAll(FeignStressConstants.WHITE_SPACE, "")));
            }
        }
        return list;
    }

    public List<String> parseHeader(Request request) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Collection<String>> header : request.headers().entrySet()) {
            String headerName = header.getKey().toUpperCase();
            if (filterTraceHeaderType(headerName)) {
                continue;
            }
            for (String headerValue : header.getValue()) {
                String value = String.format(TEMPLATE_KEY, headerName, headerValue);
                list.add(value);
            }
        }
        return list;
    }

    /**
     * 过滤请求头类型
     *
     * @param headerName
     * @return
     */
    private boolean filterTraceHeaderType(String headerName) {
        return TRACE_HEADER_LIST.contains(headerName);
    }
}