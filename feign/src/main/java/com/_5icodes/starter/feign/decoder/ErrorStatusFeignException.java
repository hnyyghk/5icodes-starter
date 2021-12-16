package com._5icodes.starter.feign.decoder;

import feign.FeignException;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class ErrorStatusFeignException extends FeignException {
    public ErrorStatusFeignException(int status, byte[] content, Map<String, Collection<String>> responseHeaders) {
        super(status, null, content, responseHeaders);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String getMessage() {
        String statusMessageFormat = "status: %s";
        try {
            if (responseBody().isPresent()) {
                return String.format(statusMessageFormat + " response body is %s", status(),
                        StandardCharsets.UTF_8.newDecoder().decode(responseBody().get()));
            } else {
                return String.format(statusMessageFormat + " with no response body", status());
            }
        } catch (CharacterCodingException e) {
            return String.format(statusMessageFormat + " with binary response body", status());
        }
    }
}