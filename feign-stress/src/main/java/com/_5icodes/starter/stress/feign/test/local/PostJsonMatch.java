package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.stress.utils.TraceTestUtils;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Content-Type: application/json 匹配处理器
 */
public class PostJsonMatch extends PostMatch {
    public PostJsonMatch(RequestBuilder requestBuilder) {
        super(requestBuilder);
    }

    @Override
    public List<String> parseMockArgs(String query) {
        return ArgsParse.parseJsonArgs(query);
    }

    @SneakyThrows
    @Override
    public void parseFeignArgs(RequestBuilder requestBuilder) {
        HttpEntity httpEntity = requestBuilder.getEntity();
        if (ObjectUtils.isEmpty(httpEntity)) {
            return;
        }
        String text = EntityUtils.toString(httpEntity);
        getFeignArgs().addAll(ArgsParse.parseJsonArgs(text));
        TraceTestUtils.info("feign.args: {}", getFeignArgs());
    }
}