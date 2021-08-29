package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.webmvc.properties.ErrorProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GlobalControllerAdviceTest {
    @Mock
    private ErrorProperties errorProperties;
    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;
    @InjectMocks
    @Spy
    private GlobalControllerAdvice advice;

    @Test
    public void handleCodeException() {
        CodeMsg codeMsg = Mockito.mock(CodeMsg.class);
        Integer code = RandomUtils.nextInt();
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(codeMsg.getCode()).thenReturn(code);
        Mockito.when(codeMsg.getMessage()).thenReturn(message);

        ResultDTO<?> resultDTO = advice.handleCodeException(codeMsg);
        Assert.assertEquals(code, resultDTO.getCode());

        Assert.assertEquals(message, resultDTO.getMessage());
    }

    @Test
    public void processBindingResult() {
        Integer code = RandomUtils.nextInt();
        Mockito.when(errorProperties.getCode()).thenReturn(code);

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        List<ObjectError> allErrors = new ArrayList<>();
        Mockito.when(bindingResult.getAllErrors()).thenReturn(allErrors);

        ObjectError error1 = Mockito.mock(ObjectError.class);
        String message1 = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(error1.getDefaultMessage()).thenReturn(message1);
        allErrors.add(error1);

        ResultDTO<?> resultDTO1 = advice.processBindingResult(bindingResult);
        Assert.assertEquals(code, resultDTO1.getCode());
        Assert.assertEquals(message1, resultDTO1.getMessage());

        ObjectError error2 = Mockito.mock(ObjectError.class);
        String message2 = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(error2.getDefaultMessage()).thenReturn(message2);
        allErrors.add(error2);

        ResultDTO<?> resultDTO2 = advice.processBindingResult(bindingResult);
        Assert.assertEquals(code, resultDTO2.getCode());
        Assert.assertEquals(message1 + "," + message2, resultDTO2.getMessage());
    }

    @Test
    public void handleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        ResultDTO<?> resultDTO = ResultDTO.setBack();

        Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);
        Mockito.doReturn(resultDTO).when(advice).processBindingResult(bindingResult);

        Assert.assertEquals(resultDTO, advice.handleMethodArgumentNotValidException(exception));
    }

    @Test
    public void handleMissParam() {
        String parameterName = RandomStringUtils.randomAlphabetic(10);
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException(parameterName, "");

        ResultDTO<?> resultDTO = advice.handleMissingServletRequestParameterException(exception);
        Assert.assertEquals(new Integer(-1), resultDTO.getCode());
        Assert.assertEquals("缺少请求参数" + parameterName, resultDTO.getMessage());
    }

    @Test
    public void handleMethodNotSupported() {
        String param = "get";
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException(param);

        ResultDTO<?> resultDTO = advice.handleHttpRequestMethodNotSupportedException(exception);
        Assert.assertEquals(new Integer(-1), resultDTO.getCode());
        Assert.assertEquals("不支持get请求方式", resultDTO.getMessage());
    }

    @Test
    public void handleBindException() {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        BindException exception = new BindException(bindingResult);
        ResultDTO<?> resultDTO = ResultDTO.setBack();

        Mockito.doReturn(resultDTO).when(advice).processBindingResult(bindingResult);

        Assert.assertEquals(resultDTO, advice.handleBindException(exception));
    }

    @Test
    public void handleProxyException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HandlerMethod handlerMethod = Mockito.mock(HandlerMethod.class);
        UndeclaredThrowableException undeclaredThrowableException = Mockito.mock(UndeclaredThrowableException.class);
        Exception exception = Mockito.mock(Exception.class);
        ModelAndView res = Mockito.mock(ModelAndView.class);

        Mockito.when(undeclaredThrowableException.getUndeclaredThrowable()).thenReturn(exception);
        Mockito.when(handlerExceptionResolver.resolveException(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(res);
        Assert.assertEquals(res, advice.handleProxyException(undeclaredThrowableException, request, response, handlerMethod));

        Throwable throwable = Mockito.mock(Throwable.class);
        Mockito.when(undeclaredThrowableException.getUndeclaredThrowable()).thenReturn(throwable);

        Object obj = new Object();
        Mockito.doReturn(obj).when(advice).handleThrowable(throwable);
        Assert.assertEquals(obj, advice.handleProxyException(undeclaredThrowableException, request, response, handlerMethod));
    }

    @Test
    public void handleThrowable() {
        String message = RandomStringUtils.randomAlphabetic(10);
        Mockito.when(errorProperties.getMessage()).thenReturn(message);
        Throwable throwable = Mockito.mock(Throwable.class);

        ResultDTO<?> resultDTO = advice.handleThrowable(throwable);
        Assert.assertEquals(new Integer(-1), resultDTO.getCode());
        Assert.assertEquals(message, resultDTO.getMessage());
    }
}