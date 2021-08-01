package com._5icodes.starter.webmvc.feign;

import com._5icodes.starter.webmvc.ErrorProperties;
import com._5icodes.starter.webmvc.SuccessProperties;
import com._5icodes.starter.webmvc.result.CodeMsgDecoder;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;

public class CodeMsgDecoderConfiguration {
    @Bean
    public Decoder decoder(Decoder delegate, SuccessProperties successProperties, ErrorProperties errorProperties) {
        return new CodeMsgDecoder(delegate, successProperties, errorProperties);
    }
}