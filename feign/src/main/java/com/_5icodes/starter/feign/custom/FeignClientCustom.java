package com._5icodes.starter.feign.custom;

import java.lang.annotation.*;

/**
 * @see com.netflix.client.config.CommonClientConfigKey
 * @see com.netflix.client.config.DefaultClientConfigImpl
 * @see org.springframework.cloud.client.loadbalancer.LoadBalancerRetryProperties
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClientCustom {
    String ReadTimeout() default "";

    String ConnectTimeout() default "";

    /**
     * 对当前实例的重试次数
     * 默认值: 0
     *
     * @return
     */
    String MaxAutoRetries() default "";

    /**
     * 切换实例的重试次数, 总的请求次数为: MaxAutoRetries + MaxAutoRetriesNextServer + (MaxAutoRetries * MaxAutoRetriesNextServer)
     * 默认值: 1
     *
     * @return
     */
    String MaxAutoRetriesNextServer() default "";

    /**
     * 对所有操作请求都进行重试
     * 默认值: false
     *
     * @return
     */
    String OkToRetryOnAllOperations() default "";

    String loggerLevel() default "";

    String errorDecoder() default "";

    String requestInterceptors() default "";

    String decode404() default "";

    String encoder() default "";

    String decoder() default "";

    String contract() default "";
}