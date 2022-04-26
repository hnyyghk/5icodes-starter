package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.stress.utils.TraceTestUtils;
import feign.Request;

import java.util.List;

/**
 * method: POST 实现匹配逻辑
 */
public abstract class PostMatch extends MockMatch {
    public PostMatch(Request request) {
        super(request);
    }

    @Override
    public boolean contains(MockData mockData) {
        List<String> mockArgs = parseMockArgs(mockData.getRequestParams());
        List<String> mockHeaders = parseMockHeaders(mockData.getHeader());
        TraceTestUtils.info("mock.args: {}", mockArgs);
        TraceTestUtils.info("mock.headers: {}", mockHeaders);
        return matchFeign(mockHeaders, mockArgs);
    }
}