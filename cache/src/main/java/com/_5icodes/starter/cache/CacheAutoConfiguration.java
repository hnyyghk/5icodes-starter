package com._5icodes.starter.cache;

import com._5icodes.starter.cache.config.LettuceClientOptionsCustomizer;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.alicp.jetcache.support.DecoderMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class CacheAutoConfiguration {
    @Bean
    public SpringConfigProvider springConfigProvider() {
        DecoderMap.register(JacksonValueEncoder.IDENTITY_NUMBER, JacksonValueDecoder.INSTANCE);
        DecoderMap.register(FastjsonValueEncoder.IDENTITY_NUMBER, FastjsonValueDecoder.INSTANCE);
        return new SpringConfigProvider() {
            @Override
            public Function<Object, byte[]> parseValueEncoder(String valueEncoder) {
                if ("JACKSON".equalsIgnoreCase(valueEncoder)) {
                    return JacksonValueEncoder.INSTANCE;
                } else if ("FASTJSON".equalsIgnoreCase(valueEncoder)) {
                    return FastjsonValueEncoder.INSTANCE;
                } else {
                    return super.parseValueEncoder(valueEncoder);
                }
            }

            @Override
            public Function<byte[], Object> parseValueDecoder(String valueDecoder) {
                if ("JACKSON".equalsIgnoreCase(valueDecoder)) {
                    return JacksonValueDecoder.INSTANCE;
                } else if ("FASTJSON".equalsIgnoreCase(valueDecoder)) {
                    return FastjsonValueDecoder.INSTANCE;
                } else {
                    return super.parseValueDecoder(valueDecoder);
                }
            }

            @Override
            public Function<Object, Object> parseKeyConvertor(String convertor) {
                if ("JACKSON".equalsIgnoreCase(convertor)) {
                    return JacksonKeyConvertor.INSTANCE;
                } else {
                    return super.parseKeyConvertor(convertor);
                }
            }
        };
    }

    @Bean
    public LettuceClientOptionsCustomizer lettuceClientOptionsCustomizer() {
        return new LettuceClientOptionsCustomizer();
    }
}