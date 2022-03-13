package com._5icodes.starter.async.callback;

import com._5icodes.starter.async.AsyncContext;

public interface AsyncCallback {
    void success(AsyncContext context);

    void failOnce(AsyncContext context);

    void failFinal(AsyncContext context);
}