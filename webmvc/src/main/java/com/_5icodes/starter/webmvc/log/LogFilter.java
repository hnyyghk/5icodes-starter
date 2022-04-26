package com._5icodes.starter.webmvc.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;

@Slf4j
public class LogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        log.debug("request uri: {}", request.getRequestURI());
        if (log.isTraceEnabled()) {
            logQueryString(request);
            logRequestHeaders(request);
            LogHttpServletRequest logHttpServletRequest = new LogHttpServletRequest(request);
            logRequestBody(logHttpServletRequest);
            LogHttpServletResponse logHttpServletResponse = new LogHttpServletResponse(response);
            filterChain.doFilter(logHttpServletRequest, logHttpServletResponse);
            logResponseHeaders(response);
            logResponseBody(logHttpServletResponse);
        } else {
            filterChain.doFilter(request, response);
        }
        log.debug("execute time: {}", System.currentTimeMillis() - start);
    }

    private void logQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null) {
            log.trace("query string: {}", queryString);
        }
    }

    private void logRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.trace("{}: {}", headerName, request.getHeader(headerName));
        }
    }

    private void logRequestBody(LogHttpServletRequest logHttpServletRequest) {
        byte[] body = logHttpServletRequest.getBody();
        int length = ArrayUtils.getLength(body);
        log.trace("{} bytes request body", length);
        if (length > 0) {
            log.trace("{}", decodeOrDefault(body, Charset.defaultCharset(), "Binary data"));
        }
    }

    private static String decodeOrDefault(byte[] body, Charset charset, String defaultValue) {
        if (body == null) {
            return defaultValue;
        }
        try {
            return charset.newDecoder().decode(ByteBuffer.wrap(body)).toString();
        } catch (CharacterCodingException e) {
            return defaultValue;
        }
    }

    private void logResponseHeaders(HttpServletResponse response) {
        log.trace("response status: {}", response.getStatus());
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            log.trace("{}: {}", headerName, response.getHeader(headerName));
        }
    }

    private void logResponseBody(LogHttpServletResponse logHttpServletResponse) {
        String str = logHttpServletResponse.reportResponse();
        if (str != null) {
            log.trace("{}", str);
        }
    }
}