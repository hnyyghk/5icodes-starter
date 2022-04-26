package com._5icodes.starter.feign.custom;

import org.springframework.http.HttpMethod;

import java.lang.annotation.*;

/**
 * @see com.netflix.client.config.CommonClientConfigKey
 * @see com.netflix.client.config.DefaultClientConfigImpl
 * @see org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties
 * @see org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration
 * @see <a href="https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#retrying-failed-requests">retrying-failed-requests</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClientCustom {
    //日志级别
    String loggerLevel() default "";

    //连接超时时间 java.net.HttpURLConnection#getConnectTimeout()，如果使用Hystrix，该配置无效
    String connectTimeout() default "";

    //读取超时时间 java.net.HttpURLConnection#getReadTimeout()，如果使用Hystrix，该配置无效
    String readTimeout() default "";

    //重试接口实现类，默认实现 feign.Retryer.Default
    String retryer() default "";

    //错误解码器
    String errorDecoder() default "";

    //请求拦截器
    String requestInterceptors() default "";

    String defaultRequestHeaders() default "";

    String defaultQueryParameters() default "";

    //是否开启404编码
    String decode404() default "";

    //解码器，将http响应转换成对象，Spring Cloud Feign使用ResponseEntityDecoder
    String decoder() default "";

    //编码器，将对象转换成http请求，Spring Cloud Feign使用SpringEncoder
    String encoder() default "";

    //处理Feign接口注解
    String contract() default "";

    String exceptionPropagationPolicy() default "";

    String capabilities() default "";

    String metrics() default "";

    String followRedirects() default "";

    /**
     * Indicates retries should be attempted on operations other than
     * {@link HttpMethod#GET}.
     */
    String retryOnAllOperations() default "";

    /**
     * Number of retries to be executed on the same <code>ServiceInstance</code>.
     */
    String maxRetriesOnSameServiceInstance() default "";

    /**
     * Number of retries to be executed on the next <code>ServiceInstance</code>. A
     * <code>ServiceInstance</code> is chosen before each retry call.
     */
    String maxRetriesOnNextServiceInstance() default "";
}