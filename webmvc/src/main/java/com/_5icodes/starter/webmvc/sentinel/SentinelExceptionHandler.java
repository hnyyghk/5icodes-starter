package com._5icodes.starter.webmvc.sentinel;

import com._5icodes.starter.webmvc.*;
import com._5icodes.starter.webmvc.result.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SentinelExceptionHandler {
    @Autowired
    @Lazy
    private ErrorProperties errorProperties;
    @Autowired
    @Lazy
    private FlowProperties flowProperties;
    @Autowired
    @Lazy
    private DegradeProperties degradeProperties;
    @Autowired
    @Lazy
    private ParamFlowProperties paramFlowProperties;
    @Autowired
    @Lazy
    private SystemBlockProperties systemBlockProperties;
    @Autowired
    @Lazy
    private AuthorityProperties authorityProperties;

    @ExceptionHandler(BlockException.class)
    public ResultDTO<?> handleBlockException(BlockException e) {
        //不同的异常返回不同的提示语
        if (e instanceof FlowException) {
            return ResultDTO.setBack(flowProperties);
        } else if (e instanceof DegradeException) {
            return ResultDTO.setBack(degradeProperties);
        } else if (e instanceof ParamFlowException) {
            return ResultDTO.setBack(paramFlowProperties);
        } else if (e instanceof SystemBlockException) {
            return ResultDTO.setBack(systemBlockProperties);
        } else if (e instanceof AuthorityException) {
            return ResultDTO.setBack(authorityProperties);
        } else {
            return ResultDTO.setBack(errorProperties);
        }
    }
}