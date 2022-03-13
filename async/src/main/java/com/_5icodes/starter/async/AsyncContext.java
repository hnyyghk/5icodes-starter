package com._5icodes.starter.async;

import com._5icodes.starter.async.policy.DelayTimeLevel;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

@Data
public class AsyncContext implements Serializable {
    private String beanName;
    private Object[] arguments;
    /**
     * 业务id
     */
    private String id;
    private DelayTimeLevel lastDelayTime;
    private Integer retryTimes;
    private Date start;
    private Boolean orderly;

    private transient Object target;
    private transient Method method;
    private transient Exception lastException;
}