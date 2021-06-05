package com._5icodes.starter.feign.client;

import com._5icodes.starter.feign.custom.FeignRequestOptionsContext;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 由于默认的ApacheHttpClient在post请求时会将url上的token以form表单形式提交, 导致对接方找不到token
 *
 * @see feign.httpclient.ApacheHttpClient
 */
public class CustomHttpClient implements Client {
    private static final String ACCEPT_HEADER_NAME = "Accept";
    private final HttpClient client;

    public CustomHttpClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        RequestBuilder requestBuilder = toHttpUriRequest(request, options);
        HttpUriRequest httpUriRequest = requestBuilder.build();
        HttpResponse httpResponse = client.execute(httpUriRequest);
        return toFeignResponse(httpResponse, request);
    }

    public RequestBuilder toHttpUriRequest(Request request, Request.Options options) {
        RequestBuilder requestBuilder = RequestBuilder.create(request.httpMethod().name());
        //custom feign timeout
        Request.Options overrideOptions = FeignRequestOptionsContext.get();
        //todo overrideOptions null
        RequestConfig requestConfig =
                (client instanceof Configurable ?
                        RequestConfig.copy(((Configurable) client).getConfig()) : RequestConfig.custom())
                        .setConnectTimeout(overrideOptions.connectTimeoutMillis())
                        .setSocketTimeout(overrideOptions.readTimeoutMillis())
                        .build();
        requestBuilder.setConfig(requestConfig);
        requestBuilder.setUri(request.url());

        // request headers
        boolean hasAcceptHeader = false;
        for (Map.Entry<String, Collection<String>> headerEntry : request.headers().entrySet()) {
            String headerName = headerEntry.getKey();
            if (headerName.equalsIgnoreCase(ACCEPT_HEADER_NAME)) {
                hasAcceptHeader = true;
            }

            if (headerName.equalsIgnoreCase(Util.CONTENT_LENGTH)) {
                // The 'Content-Length' header is always set by the Apache client and it
                // doesn't like us to set it as well.
                continue;
            }

            for (String headerValue : headerEntry.getValue()) {
                requestBuilder.addHeader(headerName, headerValue);
            }
        }
        // some servers choke on the default accept string, so we'll set it to anything
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*");
        }

        // request body
        if (request.body() != null) {
            HttpEntity entity = new ByteArrayEntity(request.body());
            requestBuilder.setEntity(entity);
        }
        return requestBuilder;
    }

    protected Response toFeignResponse(HttpResponse httpResponse, Request request) {
        StatusLine statusLine = httpResponse.getStatusLine();
        int status = statusLine.getStatusCode();
        String reason = statusLine.getReasonPhrase();
        Map<String, Collection<String>> headers = new HashMap<>();
        for (Header header : httpResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            Collection<String> headerValues = headers.computeIfAbsent(name, k -> new ArrayList<>());
            headerValues.add(value);
        }
        return Response.builder()
                .status(status)
                .reason(reason)
                .headers(headers)
                .request(request)
                .body(toFeignBody(httpResponse))
                .build();
    }

    private Response.Body toFeignBody(HttpResponse httpResponse) {
        final HttpEntity entity = httpResponse.getEntity();
        if (null == entity) {
            return null;
        }
        return new Response.Body() {
            @Override
            public Integer length() {
                return entity.getContentLength() >= 0 && entity.getContentLength() <= Integer.MAX_VALUE
                        ? (int) entity.getContentLength()
                        : null;
            }

            @Override
            public boolean isRepeatable() {
                return entity.isRepeatable();
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return entity.getContent();
            }

            @Override
            public Reader asReader(Charset charset) throws IOException {
                Util.checkNotNull(charset, "charset should not be null");
                return new InputStreamReader(asInputStream(), charset);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }
}