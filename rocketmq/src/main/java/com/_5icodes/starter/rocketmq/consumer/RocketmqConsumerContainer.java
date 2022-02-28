package com._5icodes.starter.rocketmq.consumer;

import com._5icodes.starter.common.Initable;
import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.rocketmq.RocketmqProperties;
import com._5icodes.starter.rocketmq.annotation.RocketmqListener;
import com._5icodes.starter.rocketmq.annotation.TopicSpec;
import com._5icodes.starter.rocketmq.interceptor.MessageInterceptorList;
import com._5icodes.starter.stress.StressConstants;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class RocketmqConsumerContainer extends AbstractSmartLifecycle implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private final List<DefaultMQPushConsumer> startedConsumers = new LinkedList<>();

    private final RocketmqProperties properties;

    private final MessageInterceptorList interceptorList;

    @Data
    private static class ConsumerContext {
        private DefaultMQPushConsumer consumer;
        private Boolean grayEnable;

        public ConsumerContext(DefaultMQPushConsumer consumer) {
            this.consumer = consumer;
        }
    }

    private List<Triple<
            Function<RocketmqProperties.Consumer, Object>,
            Function<RocketmqListener, Object>,
            BiConsumer<ConsumerContext, Object>
            >> propertyMapping;

    public RocketmqConsumerContainer(RocketmqProperties properties, MessageInterceptorList interceptorList) {
        this.properties = properties;
        this.interceptorList = interceptorList;
    }

    @Override
    public void doStart() {
        initPropertyMapping();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RocketmqListener.class);
        Map<String, RocketmqProperties.Consumer> consumers = properties.getConsumers();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            if (!MessageListener.class.isAssignableFrom(targetClass)) {
                continue;
            }
            RocketmqProperties.Consumer consumerProperties = consumers.get(entry.getKey());
            RocketmqListener listener = targetClass.getAnnotation(RocketmqListener.class);
            DefaultMQPushConsumer consumer = generateConsumerFromPropertyAndAnnotation(consumerProperties, listener);
            consumer.setNamesrvAddr(properties.getNameSrvAddr());
            consumer.setConsumeFromWhere(listener.fromWhere());
            consumer.setMessageModel(listener.messageModel());
            //优化clientIp生成规则，防止容器内获取的consumerClient是一样的
            String clientIp = consumer.getClientIP();
            String applicationName = SpringApplicationUtils.getApplicationName();
            consumer.setClientIP(applicationName + "-" + clientIp + "-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(100000));
            registerListener((MessageListener) bean, targetClass, consumer);
            initIfNeed(bean, targetClass, consumer);
            startConsumer(consumer);
        }
    }

    private void startConsumer(DefaultMQPushConsumer consumer) {
        try {
            consumer.start();
            startedConsumers.add(consumer);
        } catch (MQClientException e) {
            throw new BeanInitializationException("start rocketmq consumer failed", e);
        }
    }

    private void initIfNeed(Object bean, Class<?> targetClass, DefaultMQPushConsumer consumer) {
        if (!Initable.class.isAssignableFrom(targetClass)) {
            return;
        }
        Type[] interfaces = targetClass.getGenericInterfaces();
        if (ArrayUtils.isEmpty(interfaces)) {
            return;
        }
        for (Type type : interfaces) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
            if (!(actualTypeArgument instanceof Class)) {
                continue;
            }
            Class clazz = (Class) actualTypeArgument;
            if (clazz.isAssignableFrom(DefaultMQPushConsumer.class)) {
                ((Initable) bean).init(consumer);
            }
        }
    }

    private void registerListener(MessageListener bean, Class<?> targetClass, DefaultMQPushConsumer consumer) {
        if (MessageListenerConcurrently.class.isAssignableFrom(targetClass)) {
            consumer.registerMessageListener(new MessageListenerConcurrentlyWrapper((MessageListenerConcurrently) bean, interceptorList));
        } else if (MessageListenerOrderly.class.isAssignableFrom(targetClass)) {
            consumer.registerMessageListener(new MessageListenerOrderlyWrapper((MessageListenerOrderly) bean, interceptorList));
        } else {
            throw new IllegalArgumentException("MessageListener is not MessageListenerConcurrently nor MessageListenerOrderly");
        }
    }

    private DefaultMQPushConsumer generateConsumerFromPropertyAndAnnotation(RocketmqProperties.Consumer consumerProperties, RocketmqListener listener) {
        ConsumerContext consumerContext = new ConsumerContext(new DefaultMQPushConsumer());
        for (Triple<Function<RocketmqProperties.Consumer, Object>, Function<RocketmqListener, Object>, BiConsumer<ConsumerContext, Object>> triple : propertyMapping) {
            Function<RocketmqProperties.Consumer, Object> left = triple.getLeft();
            Function<RocketmqListener, Object> middle = triple.getMiddle();
            BiConsumer<ConsumerContext, Object> right = triple.getRight();
            Object o = null;
            if (consumerProperties != null) {
                o = left.apply(consumerProperties);
            }
            if (o == null && listener != null) {
                o = middle.apply(listener);
            }
            if (o != null) {
                right.accept(consumerContext, o);
            }
        }
        return consumerContext.getConsumer();
    }

    private void initPropertyMapping() {
        propertyMapping = Lists.newArrayListWithExpectedSize(6);
        //设置分组订阅
        propertyMapping.add(Triple.of(
                RocketmqProperties.Consumer::getGrayEnable,
                RocketmqListener::grayEnable,
                (consumerContext, o) -> {
                    boolean grayEnable = (Boolean) o;
                    consumerContext.setGrayEnable(grayEnable);
                }));
        //设置group
        propertyMapping.add(Triple.of(
                RocketmqProperties.Consumer::getGroup,
                RocketmqListener::group,
                (consumerContext, o) -> {
                    String group = resolve((String) o);
                    if (GrayUtils.isAppGroup() && consumerContext.getGrayEnable()) {
                        group = group + StressConstants.MQ_GRAY_SUFFIX;
                    }
                    consumerContext.getConsumer().setConsumerGroup(group);
                }));
        //设置batch大小
        propertyMapping.add(Triple.of(
                RocketmqProperties.Consumer::getBatch,
                RocketmqListener::batch,
                (consumerContext, o) -> {
                    int batch = (Integer) o;
                    consumerContext.getConsumer().setConsumeMessageBatchMaxSize(batch);
                }));
        //设置消费最小线程
        propertyMapping.add(Triple.of(
                RocketmqProperties.Consumer::getMinThread,
                RocketmqListener::minThread,
                (consumerContext, o) -> {
                    int minThread = (Integer) o;
                    if (minThread == Integer.MAX_VALUE) {
                        minThread = Runtime.getRuntime().availableProcessors() * 2;
                    }
                    consumerContext.getConsumer().setConsumeThreadMin(minThread);
                }));
        //设置消费最大线程
        propertyMapping.add(Triple.of(
                RocketmqProperties.Consumer::getMaxThread,
                RocketmqListener::maxThread,
                (consumerContext, o) -> {
                    int maxThread = (Integer) o;
                    if (maxThread == Integer.MAX_VALUE) {
                        maxThread = Runtime.getRuntime().availableProcessors() * 2;
                    }
                    consumerContext.getConsumer().setConsumeThreadMax(maxThread);
                }));
        //topic订阅
        propertyMapping.add(Triple.of(
                RocketmqProperties.Consumer::getTopics,
                listener -> {
                    TopicSpec[] topicSpecs = listener.topics();
                    List<RocketmqProperties.TopicSpec> topics = Lists.newArrayListWithExpectedSize(topicSpecs.length);
                    for (TopicSpec topicSpec : topicSpecs) {
                        topics.add(new RocketmqProperties.TopicSpec(topicSpec.topic(), topicSpec.tags(), topicSpec.sql()));
                    }
                    return topics;
                },
                (consumerContext, o) -> {
                    List<RocketmqProperties.TopicSpec> topics = (List<RocketmqProperties.TopicSpec>) o;
                    try {
                        for (RocketmqProperties.TopicSpec topic : topics) {
                            registerListenerTopic(consumerContext, topic);
                        }
                    } catch (MQClientException e) {
                        throw new BeanInitializationException("init rocketmq consumer failed", e);
                    }
                }));
    }

    /**
     * 注册监听的topic
     *
     * @param consumerContext 消费者上下文
     * @param topicSpec       topic
     */
    private void registerListenerTopic(ConsumerContext consumerContext, RocketmqProperties.TopicSpec topicSpec) throws MQClientException {
        String topic = resolve(topicSpec.getTopic());
        String tags = resolve(topicSpec.getTags());
        String sql = resolve(topicSpec.getSql());
        if (GrayUtils.isAppGroup() && consumerContext.getGrayEnable()) {
            topic = topic + StressConstants.MQ_GRAY_SUFFIX;
        }
        log.info("register group: {}, topic: {}, tags: {}, sql: {}", consumerContext.getConsumer().getConsumerGroup(), topic, tags, sql);
        MessageSelector messageSelector = StringUtils.hasText(sql) ? MessageSelector.bySql(sql) : MessageSelector.byTag(tags);
        consumerContext.getConsumer().subscribe(topic, messageSelector);
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return applicationContext.getEnvironment().resolvePlaceholders(value);
        }
        return value;
    }

    @Override
    public void doStop() {
        if (CollectionUtils.isEmpty(startedConsumers)) {
            return;
        }
        for (DefaultMQPushConsumer consumer : startedConsumers) {
            consumer.shutdown();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}