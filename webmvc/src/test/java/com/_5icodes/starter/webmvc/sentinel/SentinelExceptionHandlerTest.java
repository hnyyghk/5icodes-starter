package com._5icodes.starter.webmvc.sentinel;

import com._5icodes.starter.common.exception.CodeMsgRegistry;
import com._5icodes.starter.webmvc.properties.*;
import com._5icodes.starter.webmvc.result.ResultDTO;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SentinelExceptionHandlerTest {
    @Mock
    private ErrorProperties errorProperties;
    @Mock
    private FlowProperties flowProperties;
    @Mock
    private DegradeProperties degradeProperties;
    @Mock
    private ParamFlowProperties paramFlowProperties;
    @Mock
    private SystemBlockProperties systemBlockProperties;
    @Mock
    private AuthorityProperties authorityProperties;
    @InjectMocks
    private SentinelExceptionHandler handler;

    @Test
    public void handleFlowException() {
        FlowException exception = Mockito.mock(FlowException.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(flowProperties.getCode()).thenReturn(code);
        Mockito.when(flowProperties.getMessage()).thenReturn(message);
        CodeMsgRegistry.register(flowProperties);
        ResultDTO<?> resultDTO = handler.handleBlockException(exception);
        Assert.assertEquals(code, resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }

    @Test
    public void handleDegradeException() {
        DegradeException exception = Mockito.mock(DegradeException.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(degradeProperties.getCode()).thenReturn(code);
        Mockito.when(degradeProperties.getMessage()).thenReturn(message);
        CodeMsgRegistry.register(degradeProperties);
        ResultDTO<?> resultDTO = handler.handleBlockException(exception);
        Assert.assertEquals(code, resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }

    @Test
    public void handleParamFlowException() {
        ParamFlowException exception = Mockito.mock(ParamFlowException.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(paramFlowProperties.getCode()).thenReturn(code);
        Mockito.when(paramFlowProperties.getMessage()).thenReturn(message);
        CodeMsgRegistry.register(paramFlowProperties);
        ResultDTO<?> resultDTO = handler.handleBlockException(exception);
        Assert.assertEquals(code, resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }

    @Test
    public void handleSystemBlockException() {
        SystemBlockException exception = Mockito.mock(SystemBlockException.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(systemBlockProperties.getCode()).thenReturn(code);
        Mockito.when(systemBlockProperties.getMessage()).thenReturn(message);
        CodeMsgRegistry.register(systemBlockProperties);
        ResultDTO<?> resultDTO = handler.handleBlockException(exception);
        Assert.assertEquals(code, resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }

    @Test
    public void handleAuthorityException() {
        AuthorityException exception = Mockito.mock(AuthorityException.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(authorityProperties.getCode()).thenReturn(code);
        Mockito.when(authorityProperties.getMessage()).thenReturn(message);
        CodeMsgRegistry.register(authorityProperties);
        ResultDTO<?> resultDTO = handler.handleBlockException(exception);
        Assert.assertEquals(code, resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }

    @Test
    public void handleUnknownMockException() {
        BlockException exception = Mockito.mock(BlockException.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(errorProperties.getCode()).thenReturn(code);
        Mockito.when(errorProperties.getMessage()).thenReturn(message);
        CodeMsgRegistry.register(errorProperties);
        ResultDTO<?> resultDTO = handler.handleBlockException(exception);
        Assert.assertEquals(code, resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }
}