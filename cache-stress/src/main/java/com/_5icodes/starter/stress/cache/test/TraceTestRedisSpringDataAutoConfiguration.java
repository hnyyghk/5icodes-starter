package com._5icodes.starter.stress.cache.test;

import com._5icodes.starter.stress.cache.CacheStressConstants;
import com.alicp.jetcache.CacheBuilder;
import com.alicp.jetcache.CacheConfigException;
import com.alicp.jetcache.autoconfigure.ConfigTree;
import com.alicp.jetcache.autoconfigure.ExternalCacheAutoInit;
import com.alicp.jetcache.autoconfigure.JetCacheCondition;
import com.alicp.jetcache.external.ExternalCacheBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Map;

/**
 * @see com.alicp.jetcache.autoconfigure.RedisSpringDataAutoConfiguration
 */
@Configuration
@Conditional(TraceTestRedisSpringDataAutoConfiguration.TraceTestSpringDataRedisCondition.class)
public class TraceTestRedisSpringDataAutoConfiguration {
    public static class TraceTestSpringDataRedisCondition extends JetCacheCondition {
        public TraceTestSpringDataRedisCondition() {
            super(CacheStressConstants.TRACE_TEST_CACHE_TYPE);
        }
    }

    @Bean
    public TraceTestSpringDataRedisAutoInit traceTestSpringDataRedisAutoInit() {
        return new TraceTestSpringDataRedisAutoInit();
    }

    public static class TraceTestSpringDataRedisAutoInit extends ExternalCacheAutoInit implements ApplicationContextAware {
        private ApplicationContext applicationContext;

        public TraceTestSpringDataRedisAutoInit() {
            super(CacheStressConstants.TRACE_TEST_CACHE_TYPE);
        }

        @Override
        protected CacheBuilder initCache(ConfigTree ct, String cacheAreaWithPrefix) {
            Map<String, RedisConnectionFactory> beans = applicationContext.getBeansOfType(RedisConnectionFactory.class);
            if (beans.isEmpty()) {
                throw new CacheConfigException("no RedisConnectionFactory in spring context");
            }
            RedisConnectionFactory factory = beans.values().iterator().next();
            if (beans.size() > 1) {
                String connectionFactoryName = ct.getProperty("connectionFactory");
                if (connectionFactoryName == null) {
                    throw new CacheConfigException(
                            "connectionFactory is required, because there is multiple RedisConnectionFactory in Spring context");
                }
                if (!beans.containsKey(connectionFactoryName)) {
                    throw new CacheConfigException("there is no RedisConnectionFactory named "
                            + connectionFactoryName + " in Spring context");
                }
                factory = beans.get(connectionFactoryName);
            }
            ExternalCacheBuilder builder = new TraceTestRedisSpringDataCacheBuilder().connectionFactory(factory);
            parseGeneralConfig(builder, ct);
            return builder;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }
}