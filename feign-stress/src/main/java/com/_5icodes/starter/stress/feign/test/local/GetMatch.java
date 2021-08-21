package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.sleuth.utils.TraceTestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.RequestBuilder;

import java.util.List;

/**
 * method: GET 实现匹配逻辑
 */
public class GetMatch extends MockMatch {
    public GetMatch(RequestBuilder requestBuilder) {
        super(requestBuilder);
    }

    @Override
    public List<String> parseMockArgs(String query) {
        return ArgsParse.parseQueryArgs(query);
    }

    @Override
    public boolean contains(MockData mockData) {
        String query = StringUtils.removeStart(mockData.getUrlArgs(), "?");
        List<String> mockArgs = parseMockArgs(query);
        List<String> mockHeaders = parseMockArgs(mockData.getHeader());
        TraceTestUtils.info("mock.args: {}", mockArgs);
        TraceTestUtils.info("mock.headers: {}", mockHeaders);
        return matchFeign(mockHeaders, mockArgs);
    }

    @Override
    public void parseFeignArgs(RequestBuilder requestBuilder) {
        String query = requestBuilder.getUri().getQuery();
        getFeignArgs().addAll(ArgsParse.parseQueryArgs(query));
        TraceTestUtils.info("feign.args: {}", getFeignArgs());
    }
}