package com._5icodes.starter.stress.cache;

import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class CacheStressEnvInjector extends AbstractProfileEnvironmentPostProcessor implements Ordered {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        PropertySourceUtils.put(env, "jetcache.remote.default.type", CacheStressConstants.TRACE_TEST_CACHE_TYPE);
        super.onAllProfiles(env, application);
    }

    /**
     * 比CacheEnvInjector优先级低
     *
     * @see com._5icodes.starter.cache.CacheEnvInjector#getOrder()
     */
    @Override
    public int getOrder() {
        return 1;
    }
}