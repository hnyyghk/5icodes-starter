package com._5icodes.starter.stress.feign.test.remote;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * mock feign切面拦截
 */
@Aspect
public class MockFeignAspect {
    private final static String FEIGN_POINT_CUT = "@within(org.springframework.cloud.openfeign.FeignClient)||" +
            "@annotation(org.springframework.cloud.openfeign.FeignClient)||" +
            "this(org.springframework.cloud.openfeign.FeignClient)";

    @Pointcut(FEIGN_POINT_CUT)
    public void point() {
    }

    @Before(value = "point()")
    public void before(JoinPoint joinPoint) {
        MockUtil.set(AopTargetUtils.getTarget(joinPoint.getThis()).toString());
    }

    @After(value = "point()")
    public void after(JoinPoint joinPoint) {
        MockUtil.remove();
    }
}