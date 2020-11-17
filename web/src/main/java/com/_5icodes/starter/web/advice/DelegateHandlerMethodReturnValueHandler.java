package com._5icodes.starter.web.advice;

import com._5icodes.starter.common.utils.TraceUtils;
import com._5icodes.starter.web.WebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class DelegateHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private final HandlerMethodReturnValueHandler delegate;

    public DelegateHandlerMethodReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue instanceof ResultDTO) {
            ResultDTO resultDTO = (ResultDTO) returnValue;
            webRequest.setAttribute(WebConstants.RESULT_CODE, resultDTO.getCode(), RequestAttributes.SCOPE_REQUEST);
            resultDTO.setReqId(TraceUtils.getReqId());
        }
        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
}