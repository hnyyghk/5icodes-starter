package com._5icodes.starter.webmvc.result;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.webmvc.ErrorProperties;
import com._5icodes.starter.webmvc.SuccessProperties;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CodeMsgDecoder implements Decoder {
    private final Decoder delegate;
    private final SuccessProperties successProperties;
    private final ErrorProperties errorProperties;

    public CodeMsgDecoder(Decoder delegate, SuccessProperties successProperties, ErrorProperties errorProperties) {
        this.delegate = delegate;
        this.successProperties = successProperties;
        this.errorProperties = errorProperties;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        String str = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        Integer code;
        String message;
        String data;
        try {
            ResultDTO resultDTO = JsonUtils.parse(str, ResultDTO.class);
            code = resultDTO.getCode();
            message = resultDTO.getMessage();
            data = JsonUtils.toJson(resultDTO.getData());
        } catch (Exception e) {
            //todo
            return new ResultDTO(errorProperties.getCode(), String.format("decode feign result: %s error", str));
        }
        //todo
        if (!successProperties.getCode().equals(code)) {
            //todo
            return new ResultDTO(code, message);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (Objects.equals(parameterizedType.getRawType(), ResultDTO.class)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (ArrayUtils.getLength(actualTypeArguments) != 1) {
                    return new ResultDTO(errorProperties.getCode(),
                            "ParameterizedType ResultDTO must only have one actualTypeArgument");
                }
                Type actualTypeArgument = actualTypeArguments[0];
                if (Objects.equals(Void.class, actualTypeArgument)) {
                    return new ResultDTO(code, message);
                } else {
                    ResultDTO dto = new ResultDTO(code, message);
                    dto.setData(parseResultData(response, data, actualTypeArgument));
                    return dto;
                }
            }
        }
        return parseResultData(response, data, type);
    }

    private Object parseResultData(Response response, String data, Type type) throws IOException {
        if (StringUtils.hasText(data)) {
            Response copy = Response.builder()
                    .body(data, StandardCharsets.UTF_8)
                    .headers(response.headers())
                    .request(response.request())
                    .reason(response.reason())
                    .status(response.status()).build();
            return delegate.decode(copy, type);
        } else {
            return null;
        }
    }
}