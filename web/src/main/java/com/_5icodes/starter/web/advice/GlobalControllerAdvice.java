package com._5icodes.starter.web.advice;

import com._5icodes.starter.common.exception.BizException;
import com._5icodes.starter.common.exception.BizRuntimeException;
import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.utils.ExceptionUtils;
import com._5icodes.starter.web.ErrorProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.io.EofException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
    @Autowired
    @Lazy
    private ErrorProperties errorProperties;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    private ResultDTO fail(String message) {
        return new ResultDTO(errorProperties.getCode(), message);
    }

    @ExceptionHandler({BizException.class, BizRuntimeException.class})
    public ResultDTO handleCodeException(CodeMsg codeMsg) {
        log.warn("codeMsgException occurred, code: {}, message: {}", codeMsg.getCode(), codeMsg.getMessage());
        return new ResultDTO(codeMsg.getCode(), codeMsg.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        return fail(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultDTO handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String message = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        return fail(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultDTO handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("请求参数格式错误: {}", e.getName());
        return fail("请求参数格式错误");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultDTO handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常:", e);
        return fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResultDTO handleIllegalStateException(IllegalStateException e) {
        log.error("非法状态异常:", e);
        return fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultDTO handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        log.warn("缺少请求参数: {}", parameterName);
        return fail(String.format("缺少请求参数%s", parameterName));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultDTO handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String method = e.getMethod();
        return fail(String.format("不支持%s请求方式", method));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getLocalizedMessage());
        return fail("请求体格式错误");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResultDTO handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String headerName = e.getHeaderName();
        log.warn("缺少请求头: {}", headerName);
        return fail(String.format("缺少请求头%s", headerName));
    }

    @ExceptionHandler(EofException.class)
    public void handleEofException(EofException e) {
        log.warn("EofException occurred:", e);
    }

    @ExceptionHandler(Throwable.class)
    public ResultDTO handleThrowable(Throwable e) {
        log.error("internal error occurred:", e);
        return fail(errorProperties.getMessage());
    }

    @ExceptionHandler({UndeclaredThrowableException.class, InvocationTargetException.class})
    public Object handleProxyException(Exception e, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Throwable realException = ExceptionUtils.getRealException(e);
        if (realException instanceof Exception) {
            return handlerExceptionResolver.resolveException(request, response, handlerMethod, (Exception) realException);
        } else {
            return handleThrowable(realException);
        }
    }
}