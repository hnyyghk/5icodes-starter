package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.exception.BizException;
import com._5icodes.starter.common.exception.BizRuntimeException;
import com._5icodes.starter.common.exception.CodeMsg;
import com._5icodes.starter.common.utils.ExceptionUtils;
import com._5icodes.starter.webmvc.properties.ErrorProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.io.EofException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 */
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
    public ResultDTO<?> handleCodeException(CodeMsg codeMsg) {
        log.warn("CodeMsgException occurred, code: {}, message: {}", codeMsg.getCode(), codeMsg.getMessage());
        return new ResultDTO(codeMsg.getCode(), codeMsg.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        //jsr303 方法参数 校验异常处理
        return processBindingResult(e.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultDTO<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("请求参数格式错误: {}", e.getName());
        return fail("请求参数格式错误");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultDTO<?> handleIllegalArgumentException(IllegalArgumentException e) {
        //非法参数异常
//        e.getMessage();
        log.error("非法参数异常:", e);
        return fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResultDTO<?> handleIllegalStateException(IllegalStateException e) {
        log.error("非法状态异常:", e);
        return fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultDTO<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
//        e.getMessage();
        log.warn("缺少请求参数: {}", parameterName);
        return fail(String.format("缺少请求参数%s", parameterName));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultDTO<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String method = e.getMethod();
        return fail(String.format("不支持%s请求方式", method));
    }

    @ExceptionHandler(BindException.class)
    public ResultDTO<?> handleBindException(BindException e) {
        //jsr303 方法参数 校验异常处理
        return processBindingResult(e.getBindingResult());
    }

    ResultDTO processBindingResult(BindingResult bindingResult) {
//        bindingResult.getFieldErrors().forEach(fieldError -> {
//            fieldError.getField();
//            fieldError.getRejectedValue();
//            fieldError.getDefaultMessage();
//        });
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        return fail(message);
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultDTO<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        //Content-Type post json 请求参数不规范处理
//        e.getMessage();
        log.warn("请求体格式错误: {}", e.getLocalizedMessage());
        return fail("请求体格式错误");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResultDTO<?> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String headerName = e.getHeaderName();
        log.warn("缺少请求头: {}", headerName);
        return fail(String.format("缺少请求头%s", headerName));
    }

    @ExceptionHandler(EofException.class)
    public void handleEofException(EofException e) {
        log.warn("EofException occurred:", e);
    }

    @ExceptionHandler(Throwable.class)
    public ResultDTO<?> handleThrowable(Throwable e) {
        log.error("internal error occurred:", e);
        return ResultDTO.setBack(errorProperties);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultDTO<?> handleConstraintViolationException(ConstraintViolationException e) {
        //jsr303 validation 异常处理
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
//        constraintViolations.forEach(constraintViolation -> {
//            constraintViolation.getMessageTemplate();
//        });
        String message = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        return fail(message);
    }

    @ExceptionHandler(ServletException.class)
    public ResultDTO<?> handleServletException(ServletException e) {
        //servlet 异常
        log.error("servlet exception:", e);
        return fail(e.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    public ResultDTO<?> handleMultipartException(MultipartException e) {
        //文件上传大小异常 或 请求字节过大异常
        return fail(e.getMessage());
    }
}