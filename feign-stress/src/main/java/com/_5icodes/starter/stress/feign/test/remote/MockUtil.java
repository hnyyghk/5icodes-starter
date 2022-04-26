package com._5icodes.starter.stress.feign.test.remote;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.common.utils.SpringUtils;
import com._5icodes.starter.stress.feign.FeignStressConstants;
import com._5icodes.starter.stress.feign.test.local.MockData;
import com._5icodes.starter.stress.feign.test.local.MockSao;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

@UtilityClass
public class MockUtil {
    /**
     * mock配置文件
     */
    private final MockProperties MOCK_PROPERTIES = SpringUtils.getBean(MockProperties.class);
    /**
     * feign local
     */
    private final ThreadLocal<String> FEIGN_LOCAL = new ThreadLocal<>();

    public String get() {
        return FEIGN_LOCAL.get();
    }

    public void remove() {
        FEIGN_LOCAL.remove();
    }

    /**
     * 设置feign名称
     *
     * @param feignName
     */
    public void set(String feignName) {
        String[] arrays = feignName.split(",");
        for (String s : arrays) {
            if (s.contains(FeignStressConstants.NAME)) {
                FEIGN_LOCAL.set(StringUtils.trim(s.replace(FeignStressConstants.NAME, "")));
            }
        }
    }

    /**
     * 获取mock结果
     *
     * @param requestMapping 请求路径
     * @return MockApiDefine mock api 定义
     */
    public Boolean isMockApi(String requestMapping) {
        if (!TraceTestUtils.isTraceTest()) {
            return false;
        }
        if (!MOCK_PROPERTIES.getEnable()) {
            return false;
        }
        String feignName = get();
        List<MockApiDefine> apiDefines = MOCK_PROPERTIES.getApiDefines();
        if (CollectionUtils.isEmpty(apiDefines)) {
            return false;
        }
        MockApiDefine mockApiDefine = apiDefines.stream().filter(define -> define.getApp().equalsIgnoreCase(feignName)).findFirst().orElse(null);
        if (null == mockApiDefine) {
            return false;
        }
        if (mockApiDefine.isMockAll()) {
            return true;
        }
        List<String> mapping = mockApiDefine.getMapping();
        if (CollectionUtils.isEmpty(mapping)) {
            return false;
        }
        return mapping.stream().anyMatch(requestMapping::equalsIgnoreCase);
    }

    public List<MockData> pullAppApiList() {
        if (!MOCK_PROPERTIES.getEnable()) {
            TraceTestUtils.info("mock.enable: {}", MOCK_PROPERTIES.getEnable());
            return Collections.emptyList();
        }
        Map<String, String> request = new HashMap<>();
        request.put("active", SpringApplicationUtils.getApplicationName());
        Map<String, String> data = SpringUtils.getBean(MockSao.class).queryMockListByCall(request);
        String responseData = "data";
        List<MockData> list = new ArrayList<>();
        if (data.containsKey(responseData)) {
            list = JsonUtils.parseToList(data.get(responseData), MockData.class);
        }
        return list;
    }
}