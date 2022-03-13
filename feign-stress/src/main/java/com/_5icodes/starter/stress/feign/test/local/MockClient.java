package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.stress.feign.test.remote.MockUtil;
import com._5icodes.starter.stress.utils.TraceTestUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Args;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@Slf4j
public class MockClient {
    /**
     * 拉取api列表
     */
    public void pull() {
        try {
            List<MockData> list = MockUtil.pullAppApiList();
            if (CollectionUtils.isEmpty(list)) {
                MockClientBuilder.APIS = list;
            }
            TraceTestUtils.info("pull api list size: {}", MockClientBuilder.APIS.size());
        } catch (Exception e) {
            log.error("call mock error", e);
        }
    }

    /**
     * 获取mock服务指定路径接口定义信息
     *
     * @param service 主调方
     * @param client  被调方
     * @param path    请求路径
     * @return java.util.List<com._5icodes.starter.stress.feign.test.local.MockData>
     */
    public List<MockData> list(String service, String client, String path) {
        Args.notNull(service, "主调方");
        Args.notNull(client, "被调方");
        List<MockData> list = new ArrayList<>();
        for (MockData mockData : MockClientBuilder.APIS) {
            if (!filterMatch(service, client, path, mockData)) {
                continue;
            }
            list.add(mockData);
        }
        TraceTestUtils.info("total size: {}, name: {}, resource: [{}], size: {}", MockClientBuilder.APIS.size(), client, path, list.size());
        return list;
    }

    /**
     * mock data是否匹配
     *
     * @param service  主调方
     * @param client   被调方
     * @param path     请求路径
     * @param mockData 资源
     * @return boolean 是否匹配
     */
    private boolean filterMatch(String service, String client, String path, MockData mockData) {
        String active = StringUtils.isBlank(mockData.getActive()) ? "" : mockData.getActive();
        String passive = StringUtils.isBlank(mockData.getPassive()) ? "" : mockData.getPassive();
        return StringUtils.containsIgnoreCase(active, service)
                && StringUtils.containsIgnoreCase(passive, client)
                && path.equalsIgnoreCase(mockData.getUrlPath());
    }

    private class MockClientBuilder {
        private static List<MockData> APIS = new ArrayList<>();
    }
}