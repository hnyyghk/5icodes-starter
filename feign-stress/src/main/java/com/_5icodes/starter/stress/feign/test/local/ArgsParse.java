package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.stress.feign.FeignStressConstants;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.RequestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ArgsParse {
    /**
     * 模板key
     */
    private final String TEMPLATE_KEY = "%s=%s";
    /**
     * trace header list
     */
    private final List<String> TRACE_HEADER_LIST = Arrays.asList("X-B3-SPANID", "X-B3-PARENTSPANID", "X-B3-SAMPLED", "X-B3-TRACEID", "MODULE_ID", "ACCEPT");

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

    public List<String> parseHeader(RequestBuilder requestBuilder) {
        List<String> list = new ArrayList<>();
        for (Header header : requestBuilder.build().getAllHeaders()) {
            if (filterTraceHeaderType(header.getName())) {
                continue;
            }
            String value = String.format(TEMPLATE_KEY, header.getName().toUpperCase(), header.getValue());
            list.add(value);
        }
        return list;
    }

    /**
     * 过滤请求头类型
     *
     * @param name
     * @return
     */
    private boolean filterTraceHeaderType(String name) {
        return TRACE_HEADER_LIST.contains(name.toUpperCase());
    }
}