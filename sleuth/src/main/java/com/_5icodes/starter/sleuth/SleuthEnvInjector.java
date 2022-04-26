package com._5icodes.starter.sleuth;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.CommonConstants;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SleuthEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    /**
     * sleuth自定义链路跟踪字段调整，以及跟踪频率设置调整
     *
     * @param env
     * @param application
     * @see <a href="https://github.com/spring-cloud/spring-cloud-sleuth/wiki/Spring-Cloud-Sleuth-3.0-Migration-Guide#baggage">baggage</a>
     */
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        //从上下文获取时大小写不敏感，通过MDC.get()与BaggageFieldUtils.get()查询时大小写敏感
        List<String> correlationFields = Arrays.asList(SleuthConstants.CLIENT_IP, CommonConstants.APP_GROUP);
        //供日志打印使用字段，必须在remote-fields中，可由MDC.get()查询
        PropertySourceUtils.put(env, "spring.sleuth.baggage.correlation-fields", correlationFields);
        List<String> remoteFields = new ArrayList<>(correlationFields);
        remoteFields.addAll(Arrays.asList(SleuthConstants.X_USER_INFO, SleuthConstants.TRACE_TEST_HEADER));
        //上下游请求过程中携带字段,MQ生产消费过程中携带字段,可由BaggageFieldUtils.get()查询
        PropertySourceUtils.put(env, "spring.sleuth.baggage.remote-fields", remoteFields);
        //链路数据生产频率设置
        PropertySourceUtils.put(env, "spring.sleuth.reactor.instrumentation-type", "DECORATE_ON_EACH");
        super.onAllProfiles(env, application);
    }
}