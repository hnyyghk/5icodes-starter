package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.utils.TraceUtils;
import com._5icodes.starter.webmvc.properties.ErrorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${server.error.path:${error:path:/error}}")
@Slf4j
public class ErrorController extends AbstractErrorController {
    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
    private final ErrorProperties errorProperties;

    public ErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes);
        this.errorProperties = errorProperties;
    }

    @RequestMapping
    public ResultDTO<?> error(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable error = getError(webRequest);
        String msg = null;
        if (error != null) {
            log.error("internal error occurred", error);
            msg = error.getMessage();
        }
        if (msg == null) {
            msg = getAttribute(webRequest, "javax.servlet.error.message");
        }
        if (msg == null) {
            msg = errorProperties.getMessage();
        }
        Integer status = getAttribute(webRequest, "javax.servlet.error.status_code");
        if (status != null) {
            response.setStatus(status);
        }
        return new ResultDTO<>(errorProperties.getCode(), msg).setReqId(TraceUtils.getReqId());
    }

    private Throwable getError(WebRequest webRequest) {
        Throwable throwable = getAttribute(webRequest, ERROR_ATTRIBUTE);
        if (throwable == null) {
            throwable = getAttribute(webRequest, "javax.servlet.error.exception");
        }
        return throwable;
    }

    private <T> T getAttribute(WebRequest webRequest, String name) {
        return (T) webRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}