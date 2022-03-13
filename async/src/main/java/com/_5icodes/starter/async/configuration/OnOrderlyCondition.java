package com._5icodes.starter.async.configuration;

import com._5icodes.starter.async.AsyncConstants;
import com._5icodes.starter.async.AsyncProperties;
import com._5icodes.starter.common.condition.AbstractEnabledSpringBootCondition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class OnOrderlyCondition extends AbstractEnabledSpringBootCondition<AsyncProperties> {
    public OnOrderlyCondition() {
        super(AsyncConstants.PROPERTY_PREFIX,
                AsyncProperties.class,
                OnOrderlyCondition::checkGroupTopicPresent);
    }

    public static boolean checkGroupTopicPresent(AsyncProperties asyncProperties) {
        return asyncProperties.getOrder() != null
                && StringUtils.hasText(asyncProperties.getOrder().getGroup())
                && !CollectionUtils.isEmpty(asyncProperties.getOrder().getTopics())
                && asyncProperties.getOrder().getTopics().stream().allMatch(topicSpec -> StringUtils.hasText(topicSpec.getTopic()));
    }
}