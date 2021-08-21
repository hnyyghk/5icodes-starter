package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.stress.feign.test.remote.MockUtil;
import com._5icodes.starter.sleuth.utils.TraceTestUtils;
import lombok.Data;
import org.apache.http.client.methods.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * mock处理实现
 */
@Data
public abstract class MockMatch {
    /**
     * mock指定主调, 被调, 请求路径, mock结果集
     */
    private List<MockData> mockApiList = new ArrayList<>();
    /**
     * feign get请求头集合
     */
    private List<String> feignHeaders = new ArrayList<>();
    /**
     * feign get请求参数集合
     */
    private List<String> feignArgs = new ArrayList<>();
    /**
     * 请求参数
     */
    private final RequestBuilder requestBuilder;

    public MockMatch(RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        parseMockDataList();
        parseFeignArgs(requestBuilder);
        parseFeignHeaders();
    }

    /**
     * 获取匹配规则的mock定义数据
     *
     * @return 匹配的结果集
     */
    public MockData match() {
        return getMockApiList().stream()
                .filter(this::contains)
                .findFirst()
                .orElseThrow(() -> new MockNotSupportException(getMockNotSupportMessage()));
    }

    /**
     * 是否包含关系
     *
     * @param mockData mock数据
     * @return boolean
     */
    public abstract boolean contains(MockData mockData);

    /**
     * 解析mock请求参数
     *
     * @param query 请求参数
     * @return 参数列表
     */
    public abstract List<String> parseMockArgs(String query);

    /**
     * 解析mock请求头
     *
     * @param header 请求头
     * @return 参数列表
     */
    public List<String> parseMockHeaders(String header) {
        return ArgsParse.parseHeader(header);
    }

    /**
     * 判断
     * feign请求入参是否包含mock请求入参
     * feign请求头是否包含mock请求头
     *
     * @param mockHeaders mock请求头
     * @param mockArgs    mock请求入参
     * @return boolean 匹配结果
     */
    public boolean matchFeign(List<String> mockHeaders, List<String> mockArgs) {
        boolean compare = feignHeaders.containsAll(mockHeaders) && feignArgs.containsAll(mockArgs);
        TraceTestUtils.info(TraceTestUtils.isTraceTest(), "match result: {}", compare);
        return compare;
    }

    /**
     * 获取远程mock api list
     */
    public void parseMockDataList() {
        mockApiList.addAll(MockClient.list(SpringApplicationUtils.getApplicationName(), MockUtil.get(), requestBuilder.getUri().getPath()));
    }

    /**
     * 获取解析请求参数
     *
     * @param requestBuilder 请求参数
     */
    public abstract void parseFeignArgs(RequestBuilder requestBuilder);

    /**
     * 解析feign请求头
     */
    public void parseFeignHeaders() {
        feignHeaders.addAll(ArgsParse.parseHeader(requestBuilder));
        TraceTestUtils.info("feign.header: {}", feignHeaders);
    }

    /**
     * 获取mock不再支持的消息
     *
     * @return java.lang.String
     */
    public String getMockNotSupportMessage() {
        String path = requestBuilder.getUri().getPath();
        return String.format("not support feign: %s, path: %s, args: %s, headers: %s, match api size: %s",
                MockUtil.get(), path, feignArgs, feignHeaders, mockApiList.size());
    }
}