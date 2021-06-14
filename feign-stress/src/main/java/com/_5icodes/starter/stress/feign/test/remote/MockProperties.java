package com._5icodes.starter.stress.feign.test.remote;

import com._5icodes.starter.stress.feign.FeignStressConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = FeignStressConstants.MOCK_PREFIX)
public class MockProperties {
    /**
     * 是否启用mock开关
     */
    private Boolean enable = false;
    /**
     * 时间
     */
    private Long timeout = 50L;
    /**
     * mock server地址
     */
    private List<MockApiDefine> apiDefines;
}