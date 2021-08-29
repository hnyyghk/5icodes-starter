package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.exception.CodeMsgRegistry;
import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.SpringUtils;
import com._5icodes.starter.webmvc.properties.SuccessProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.io.File;
import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.script.*"})
public class CodeMsgResponseBodyAdviceTest {
    @InjectMocks
    private CodeMsgResponseBodyAdvice advice;

    @Test
    public void supports() {
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        Assert.assertTrue(advice.supports(parameter, MappingJackson2HttpMessageConverter.class));
        Assert.assertTrue(advice.supports(parameter, StringHttpMessageConverter.class));

        Mockito.doReturn(CodeMsgResponseBodyAdviceTest.class).when(parameter).getDeclaringClass();
        advice.addExcludeClasses(Collections.singleton(CodeMsgResponseBodyAdviceTest.class.getName()));
        Assert.assertFalse(advice.supports(parameter, StringHttpMessageConverter.class));
    }

    @Test
    @PrepareForTest(SpringUtils.class)
    public void beforeBodyWrite() {
        SuccessProperties codeMsg = Mockito.mock(SuccessProperties.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(codeMsg.getCode()).thenReturn(code);
        Mockito.when(codeMsg.getMessage()).thenReturn(message);

        CodeMsgRegistry.register(codeMsg);

        PowerMockito.mockStatic(SpringUtils.class);
        PowerMockito.when(SpringUtils.getBean(SuccessProperties.class)).thenReturn(codeMsg);
        PowerMockito.when(SpringUtils.getBean(ObjectMapper.class)).thenReturn(new ObjectMapper());

        String reqId = RandomStringUtils.randomAlphabetic(10);
        MDC.put("traceId", reqId);
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        MediaType mediaType = Mockito.mock(MediaType.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        {
            Object obj = advice.beforeBodyWrite(null, parameter, mediaType, MappingJackson2HttpMessageConverter.class, request, response);
            Assert.assertTrue(obj instanceof ResultDTO);
            ResultDTO resultDTO = (ResultDTO) obj;
            Assert.assertEquals(reqId, resultDTO.getReqId());
            Assert.assertEquals(code, resultDTO.getCode());
            Assert.assertEquals(message, resultDTO.getMessage());
            Assert.assertNull(resultDTO.getData());
        }
        {
            ResultDTO res = Mockito.mock(ResultDTO.class);
            Object obj = advice.beforeBodyWrite(res, parameter, mediaType, MappingJackson2HttpMessageConverter.class, request, response);
            Assert.assertTrue(obj instanceof ResultDTO);
            ResultDTO resultDTO = (ResultDTO) obj;
            Mockito.verify(res).setReqId(reqId);
            Assert.assertEquals(res, resultDTO);
        }
        {
            String str = RandomStringUtils.randomAlphabetic(5);
            Object obj = advice.beforeBodyWrite(str, parameter, mediaType, StringHttpMessageConverter.class, request, response);
            Assert.assertTrue(obj instanceof String);
            ResultDTO resultDTO = JsonUtils.parse((String) obj, ResultDTO.class);
            Assert.assertEquals(reqId, resultDTO.getReqId());
            Assert.assertEquals(code, resultDTO.getCode());
            Assert.assertEquals(message, resultDTO.getMessage());
            Assert.assertEquals(str, resultDTO.getData());
        }
        {
            File file = Mockito.mock(File.class);
            Object obj = advice.beforeBodyWrite(file, parameter, mediaType, MappingJackson2HttpMessageConverter.class, request, response);
            Assert.assertEquals(file, obj);
        }
        {
            Object result = new Object();
            Object obj = advice.beforeBodyWrite(result, parameter, mediaType, MappingJackson2HttpMessageConverter.class, request, response);
            Assert.assertTrue(obj instanceof ResultDTO);
            ResultDTO resultDTO = (ResultDTO) obj;
            Assert.assertEquals(reqId, resultDTO.getReqId());
            Assert.assertEquals(code, resultDTO.getCode());
            Assert.assertEquals(message, resultDTO.getMessage());
            Assert.assertEquals(result, resultDTO.getData());
        }
    }
}