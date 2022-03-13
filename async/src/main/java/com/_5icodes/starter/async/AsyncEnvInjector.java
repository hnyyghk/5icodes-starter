package com._5icodes.starter.async;

import com._5icodes.starter.async.configuration.OnConcurrentlyCondition;
import com._5icodes.starter.async.configuration.OnOrderlyCondition;
import com._5icodes.starter.common.AbstractProfileEnvironmentPostProcessor;
import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.rocketmq.RocketmqConstants;
import com._5icodes.starter.rocketmq.RocketmqProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

public class AsyncEnvInjector extends AbstractProfileEnvironmentPostProcessor {
    @Override
    protected void onAllProfiles(ConfigurableEnvironment env, SpringApplication application) {
        Binder binder = Binder.get(env);
        BindResult<AsyncProperties> bindResult = binder.bind(AsyncConstants.PROPERTY_PREFIX, AsyncProperties.class);
        if (!bindResult.isBound()) {
            return;
        }
        AsyncProperties asyncProperties = bindResult.get();
        if (OnConcurrentlyCondition.checkGroupTopicPresent(asyncProperties)) {
            process(env, AsyncConstants.MESSAGE_LISTENER_CONCURRENTLY_BEAN_NAME, asyncProperties);
        }
        if (OnOrderlyCondition.checkGroupTopicPresent(asyncProperties)) {
            process(env, AsyncConstants.MESSAGE_LISTENER_ORDERLY_BEAN_NAME, asyncProperties.getOrder());
        }
        super.onAllProfiles(env, application);
    }

    private void process(ConfigurableEnvironment env, String beanName, RocketmqProperties.Consumer consumer) {
        PropertySourceUtils.put(env, RocketmqConstants.PROPERTY_PREFIX + ".consumers." + beanName + ".group", consumer.getGroup());
        if (consumer.getMinThread() != null && consumer.getMinThread() > 0) {
            PropertySourceUtils.put(env, RocketmqConstants.PROPERTY_PREFIX + ".consumers." + beanName + ".minThread", consumer.getMinThread());
        }
        if (consumer.getMaxThread() != null && consumer.getMaxThread() > 0) {
            PropertySourceUtils.put(env, RocketmqConstants.PROPERTY_PREFIX + ".consumers." + beanName + ".maxThread", consumer.getMaxThread());
        }
        PropertySourceUtils.put(env, RocketmqConstants.PROPERTY_PREFIX + ".consumers." + beanName + ".topics[0]", consumer.getTopics().get(0));
    }
}