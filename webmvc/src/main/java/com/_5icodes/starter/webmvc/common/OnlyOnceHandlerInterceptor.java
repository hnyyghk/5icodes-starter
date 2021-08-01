package com._5icodes.starter.webmvc.common;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface OnlyOnceHandlerInterceptor extends HandlerInterceptor {
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isFirstRequest(request)) {
            return doPreHandle(request, response, handler);
        } else {
            return true;
        }
    }

    default boolean isFirstRequest(HttpServletRequest request) {
        DispatcherType dispatcherType = request.getDispatcherType();
        return dispatcherType.equals(DispatcherType.REQUEST) || dispatcherType.equals(DispatcherType.ASYNC);
    }

    default boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (isFirstRequest(request)) {
            doAfterCompletion(request, response, handler, ex);
        }
    }

    default void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    @Override
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (isFirstRequest(request)) {
            doPostHandle(request, response, handler, modelAndView);
        }
    }

    default void doPostHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
}