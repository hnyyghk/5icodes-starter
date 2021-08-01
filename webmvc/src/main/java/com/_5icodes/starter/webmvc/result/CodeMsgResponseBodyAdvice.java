package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.TraceUtils;
import com._5icodes.starter.webmvc.WebMvcConstants;
import com._5icodes.starter.webmvc.advice.NotAutoWrap;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

public class CodeMsgResponseBodyAdvice implements ResponseBodyAdvice<Object>, ApplicationContextAware {
    private Set<String> excludeClasses;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Object> beans = context.getBeansWithAnnotation(NotAutoWrap.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class<?> userClass = ClassUtils.getUserClass(entry.getValue().getClass());
            addExcludeClasses(Collections.singleton(userClass.getName()));
        }
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        if (!converterSupports(aClass)) {
            return false;
        }
        if (null == excludeClasses) {
            return true;
        }
        String name = methodParameter.getDeclaringClass().getName();
        return !excludeClasses.contains(name);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof ResultDTO) {
            ResultDTO resultDTO = (ResultDTO) body;
            return handleResultDTO(resultDTO, serverHttpRequest);
        } else if (body instanceof File) {
            return body;
        } else if (StringHttpMessageConverter.class.isAssignableFrom(aClass)) {
            ResultDTO<Object> resultDTO = ResultDTO.setBackWithData(body);
            return JsonUtils.toJson(handleResultDTO(resultDTO, serverHttpRequest));
        } else {
            ResultDTO<Object> resultDTO = ResultDTO.setBackWithData(body);
            return handleResultDTO(resultDTO, serverHttpRequest);
        }
    }

    private ResultDTO handleResultDTO(ResultDTO resultDTO, ServerHttpRequest serverHttpRequest) {
        resultDTO.setReqId(TraceUtils.getReqId());
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            servletRequest.setAttribute(WebMvcConstants.RESULT_CODE, resultDTO.getCode());
        }
        return resultDTO;
    }

    public void addExcludeClasses(Set<String> classNameList) {
        if (null == excludeClasses) {
            excludeClasses = new HashSet<>();
        }
        excludeClasses.addAll(classNameList);
    }

    private boolean converterSupports(Class<? extends HttpMessageConverter<?>> aClass) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(aClass) ||
                StringHttpMessageConverter.class.isAssignableFrom(aClass);
    }
}