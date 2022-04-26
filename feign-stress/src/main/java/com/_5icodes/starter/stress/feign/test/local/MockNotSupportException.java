package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.common.exception.BizRuntimeException;
import com._5icodes.starter.common.utils.SpringUtils;

public class MockNotSupportException extends BizRuntimeException {
    public MockNotSupportException(Object... arguments) {
        super(SpringUtils.getBean(MockNotSupportProperties.class), arguments);
    }
}