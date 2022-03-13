package com._5icodes.starter.async.configuration;

import com._5icodes.starter.async.AsyncConstants;
import com._5icodes.starter.async.AsyncProperties;
import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class OnConcurrentlyCondition extends AbstractEnabledSpringBootCondition<AsyncProperties> {
    public OnConcurrentlyCondition() {
        super(AsyncConstants.PROPERTY_PREFIX,
                AsyncProperties.class,
                OnConcurrentlyCondition::checkGroupTopicPresent);
    }

    public static boolean checkGroupTopicPresent(AsyncProperties asyncProperties) {
        return StringUtils.hasText(asyncProperties.getGroup())
                && !CollectionUtils.isEmpty(asyncProperties.getTopics())
                && asyncProperties.getTopics().stream().allMatch(topicSpec -> StringUtils.hasText(topicSpec.getTopic()));
    }
}