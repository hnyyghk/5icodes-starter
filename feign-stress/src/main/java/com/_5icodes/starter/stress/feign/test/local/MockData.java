package com._5icodes.starter.stress.feign.test.local;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * mock平台请求定义
 */
@Data
public class MockData {
    /**
     * 主键
     */
    @JsonProperty("url_id")
    private Integer urlId;
    /**
     * 接口名称
     */
    private String name;
    /**
     * 主调方
     */
    private String active;
    /**
     * 被调方
     */
    private String passive;
    /**
     * 调用方法
     */
    @JsonProperty("call_method")
    private String callMethod;
    /**
     * 调用地址
     */
    private String address;
    /**
     * 服务名称
     */
    @JsonProperty("service_name")
    private String serviceName;
    /**
     * url路径
     */
    @JsonProperty("url_path")
    private String urlPath;
    /**
     * url参数
     */
    @JsonProperty("url_args")
    private String urlArgs;
    /**
     * 是否完全匹配
     */
    @JsonProperty("if_exact_match")
    private String ifExactMatch;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求头
     */
    private String header;
    /**
     * 请求参数
     */
    @JsonProperty("request_params")
    private String requestParams;
    /**
     * post提交类型: Content_Type
     */
    @JsonProperty("post_para_type")
    private String postParaType;
    /**
     * 响应参数
     */
    @JsonProperty("response_params")
    private String responseParams;
    /**
     * 响应码
     */
    @JsonProperty("response_status_code")
    private Integer responseStatusCode;
    /**
     * 是否加密
     */
    @JsonProperty("is_encryption")
    private String isEncryption;
    /**
     * 加密方法
     */
    @JsonProperty("encryption_method")
    private String encryptionMethod;
    /**
     * 休眠时间
     */
    @JsonProperty("sleep_time")
    private Long sleepTime;
    /**
     * 状态: 1-有效, 0-无效
     */
    private Integer status;
    /**
     * url是否包含参数
     */
    @JsonProperty("url_contain_param")
    private Integer urlContainParam;
    /**
     * 备注
     */
    private String note;
    /**
     * 是否删除: 1-删除, 0-未删除
     */
    private Integer delete;

    /**
     * 获取兜底返回体
     *
     * @return
     */
    private static MockData getDefaultMockData() {
        MockData mockData = new MockData();
        mockData.setUrlId(0);
        mockData.setResponseStatusCode(200);
        mockData.setResponseParams("{}");
        mockData.setSleepTime(30L);
        return mockData;
    }
}