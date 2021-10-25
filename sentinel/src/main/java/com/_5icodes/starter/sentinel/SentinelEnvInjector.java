package com._5icodes.starter.sentinel;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import com.alibaba.cloud.sentinel.datasource.RuleType;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SentinelEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected boolean shouldProcess(ConfigurableEnvironment env, SpringApplication application) {
        return super.shouldProcess(env, application) && env.getProperty(SentinelConstants.PROPERTY_ENABLED, Boolean.class, true);
    }

    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        //默认启动feign的sentinel全局开关
        PropertySourceUtils.put(env, "feign.sentinel.enabled", true);
        PropertySourceUtils.put(env, "spring.cloud.sentinel.enabled", false);
        //禁用spring cloud alibaba默认启动的filter拦截
        PropertySourceUtils.put(env, "spring.cloud.sentinel.filter.enabled", false);
        //配置基础apollo的动态降级限流规则
        String applicationName = SpringApplicationUtils.getApplicationName();
        List<RuleType> values = Arrays.asList(RuleType.FLOW, RuleType.DEGRADE, RuleType.PARAM_FLOW, RuleType.SYSTEM, RuleType.AUTHORITY);
        for (int i = 0; i < values.size(); i++) {
            initApolloDataSource(env, applicationName, values.get(i), i + 1);
        }
        //默认启动的sentinel监控数据查询端口
        PropertySourceUtils.put(env, "spring.cloud.sentinel.transport.port", 15719);
        //sentinel的缓存数据目录
        PropertySourceUtils.put(env, "spring.cloud.sentinel.log.dir", "/data/logs/" + applicationName);
        super.onAllProfiles(env, application);
    }

    private void initApolloDataSource(ConfigurableEnvironment env, String applicationName, RuleType ruleType, int count) {
        String ruleTypeName = ruleType.getName();
        PropertySourceUtils.put(env, "spring.cloud.sentinel.datasource.ds" + count + ".apollo.namespace-name", "application");
        PropertySourceUtils.put(env, "spring.cloud.sentinel.datasource.ds" + count + ".apollo.flow-rules-key", applicationName + "-" + ruleTypeName + "-rules");
        PropertySourceUtils.put(env, "spring.cloud.sentinel.datasource.ds" + count + ".apollo.default-flow-rule-value", "[]");
        PropertySourceUtils.put(env, "spring.cloud.sentinel.datasource.ds" + count + ".apollo.rule-type", ruleTypeName);
    }

    @Override
    protected void onLocal(ConfigurableEnvironment env, SpringApplication application) {
        setDashboardServer(env, "10.25.80.45:8060");
        super.onLocal(env, application);
    }

    private void setDashboardServer(ConfigurableEnvironment env, String server) {
        PropertySourceUtils.put(env, "spring.cloud.sentinel.transport.dashboard", server);
    }
}