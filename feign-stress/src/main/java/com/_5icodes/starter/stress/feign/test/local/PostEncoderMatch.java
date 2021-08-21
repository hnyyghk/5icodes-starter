package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.sleuth.utils.TraceTestUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Content-Type: application/x-www-form-urlencoded 匹配处理器
 */
public class PostEncoderMatch extends PostMatch {
    public PostEncoderMatch(RequestBuilder requestBuilder) {
        super(requestBuilder);
    }

    @Override
    public List<String> parseMockArgs(String query) {
        return query.contains("{") ? ArgsParse.parseJsonArgs(query) : ArgsParse.parseQueryArgs(query);
    }

    @SneakyThrows
    @Override
    public void parseFeignArgs(RequestBuilder requestBuilder) {
        HttpEntity httpEntity = requestBuilder.getEntity();
        String text = EntityUtils.toString(httpEntity);
        if (StringUtils.isNotBlank(text)) {
            text = URLDecoder.decode(text, StandardCharsets.UTF_8.name());
            getFeignArgs().addAll(ArgsParse.parseQueryArgs(text));
        }
        TraceTestUtils.info("feign.args: {}", getFeignArgs());
    }
}