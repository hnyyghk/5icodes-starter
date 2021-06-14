package com._5icodes.starter.stress.feign.test;

import com._5icodes.starter.feign.client.CustomHttpClient;
import com._5icodes.starter.stress.feign.test.local.MockHelper;
import com._5icodes.starter.stress.feign.test.remote.MockUtil;
import feign.Client;
import feign.Request;
import feign.Response;
import org.apache.http.client.methods.RequestBuilder;

import java.io.IOException;
import java.net.URI;

public class CustomStressHttpClient implements Client {
    private final CustomHttpClient customHttpClient;

    public CustomStressHttpClient(CustomHttpClient customHttpClient) {
        this.customHttpClient = customHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        if (MockUtil.isMockApi(URI.create(request.url()).getPath())) {
            RequestBuilder requestBuilder = customHttpClient.toHttpUriRequest(request, options);
            return MockHelper.handle(request, requestBuilder);
        }
        return customHttpClient.execute(request, options);
    }
}