package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.stress.utils.TraceTestUtils;
import feign.Request;
import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Content-Type: application/x-www-form-urlencoded 匹配处理器
 */
public class PostEncoderMatch extends PostMatch {
    public PostEncoderMatch(Request request) {
        super(request);
    }

    @Override
    public List<String> parseMockArgs(String query) {
        return query.contains("{") ? ArgsParse.parseJsonArgs(query) : ArgsParse.parseQueryArgs(query);
    }

    @SneakyThrows
    @Override
    public void parseFeignArgs() {
        Request request = getRequest();
        if (request.body() != null) {
            String text = new String(request.body(), request.charset());
            text = URLDecoder.decode(text, StandardCharsets.UTF_8.name());
            getFeignArgs().addAll(ArgsParse.parseQueryArgs(text));
        }
        TraceTestUtils.info("feign.args: {}", getFeignArgs());
    }
}