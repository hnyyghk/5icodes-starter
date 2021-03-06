package com._5icodes.starter.cache;

import com._5icodes.starter.cache.config.LettuceClientOptionsCustomizer;
import com._5icodes.starter.cache.test.EmbeddedRedisServer;
import com._5icodes.starter.cache.test.RedisConnectFactoryLifecycle;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.common.utils.snowflake.SnowflakeIdGenerator;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.alicp.jetcache.support.DecoderMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.embedded.RedisServer;

import java.util.function.Function;

@Configuration
@Slf4j
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
    @ConditionalOnMissingBean
    public SnowflakeIdGenerator snowflakeIdGenerator(StringRedisTemplate stringRedisTemplate) {
        try {
            long customId = stringRedisTemplate.opsForValue().increment(CacheConstants.SNOW_FLAKE_REDIS_PREFIX + SpringApplicationUtils.getApplicationName());
            return new SnowflakeIdGenerator(customId);
        } catch (Exception e) {
            log.error("init snowflakeIdGenerator error", e);
        }
        return null;
    }

    @Bean
    public LettuceClientOptionsCustomizer lettuceClientOptionsCustomizer() {
        return new LettuceClientOptionsCustomizer();
    }

    @ConditionalOnClass(RedisServer.class)
    @Configuration
    @Profile("it")
    public static class EmbeddedRedisServerAutoConfiguration {
        @Bean(destroyMethod = "stop", name = CacheConstants.EMBEDDED_REDIS_SERVER_BEAN)
        @ConditionalOnMissingBean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public static EmbeddedRedisServer embeddedRedisServer() {
            return new EmbeddedRedisServer(6379, 20);
        }

        @Bean
        public RedisConnectFactoryLifecycle redisConnectFactoryLifecycle(LettuceConnectionFactory connectionFactory) {
            return new RedisConnectFactoryLifecycle(connectionFactory);
        }
    }
}