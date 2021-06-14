package com._5icodes.starter.stress.feign.test.local;

import com._5icodes.starter.common.exception.BizRuntimeException;

public class MockNotSupportException extends BizRuntimeException {
    public MockNotSupportException(Object... arguments) {
        super(MockNotSupportCodeMsgEnums.MOCK_NOT_SUPPORT, arguments);
    }
}