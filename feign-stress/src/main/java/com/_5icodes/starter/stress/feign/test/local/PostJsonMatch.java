package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.stress.utils.TraceTestUtils;
import feign.Request;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Content-Type: application/json 匹配处理器
 */
public class PostJsonMatch extends PostMatch {
    public PostJsonMatch(Request request) {
        super(request);
    }

    @Override
    public List<String> parseMockArgs(String query) {
        return ArgsParse.parseJsonArgs(query);
    }

    @SneakyThrows
    @Override
    public void parseFeignArgs() {
        Request request = getRequest();
        if (request.body() != null) {
            String text = new String(request.body(), request.charset());
            getFeignArgs().addAll(ArgsParse.parseJsonArgs(text));
        }
        TraceTestUtils.info("feign.args: {}", getFeignArgs());
    }
}