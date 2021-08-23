package com._5icodes.starter.webmvc.monitor;

import com._5icodes.starter.monitor.ExceptionReport;
import com.alibaba.csp.sentinel.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExceptionReportResolver implements HandlerExceptionResolver, Ordered {
    @Autowired
    private ExceptionReport exceptionReport;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        boolean report = exceptionReport.report("webmvc:" + request.getRequestURI(), e);
        if (report) {
            Tracer.trace(e);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}