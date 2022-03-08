package com._5icodes.starter.kafka.trace;

import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.kafka.KafkaConstants;
import com._5icodes.starter.kafka.KafkaProperties;
import com._5icodes.starter.stress.StressConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class TraceTestListenerBeanPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {
    private final CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider;

    private KafkaProperties properties;
    private org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties;

    public TraceTestListenerBeanPostProcessor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        this.metadataReaderFactoryProvider = metadataReaderFactoryProvider;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (null == properties || CollectionUtils.isEmpty(properties.getGrayTopics())) {
            return;
        }
        metadataReaderFactoryProvider.processMetadataReader(new Consumer<MetadataReader>() {
            @Override
            @SneakyThrows
            public void accept(MetadataReader metadataReader) {
                if (!metadataReader.getAnnotationMetadata().hasAnnotatedMethods(KafkaListener.class.getName()) &&
                        !metadataReader.getAnnotationMetadata().hasAnnotatedMethods(KafkaListeners.class.getName())) {
                    return;
                }
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> aClass = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
                //@KafkaListeners({@KafkaListener(topics = "TEST_TOPIC"), @KafkaListener(topics = "TEST_TOPIC_2")})
                //或
                //@KafkaListener(topics = "TEST_TOPIC")
                //@KafkaListener(topics = "TEST_TOPIC_2")
                //或
                //@KafkaListener(topics = "TEST_TOPIC")
                ReflectionUtils.doWithLocalMethods(aClass, new ReflectionUtils.MethodCallback() {
                    @Override
                    @SneakyThrows
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        //JDK8的重复注解特性@Repeatable，通过getAnnotationsByType()来返回重复注解的类型
                        KafkaListener[] annotations = method.getAnnotationsByType(KafkaListener.class);
                        if (annotations == null || annotations.length == 0) {
                            return;
                        }
                        for (KafkaListener kafkaListener : annotations) {
                            InvocationHandler invocationHandler = Proxy.getInvocationHandler(kafkaListener);
                            Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
                            memberValuesField.setAccessible(true);
                            Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
                            String[] topics = (String[]) memberValues.get("topics");
                            if (topics == null || topics.length == 0) {
                                //仅支持topics形式，不支持topicPattern及topicPartitions形式
                                return;
                            }
                            List<String> grayTopics = properties.getGrayTopics();
                            boolean grayEnable = false;
                            for (int i = 0; i < topics.length; i++) {
                                if (GrayUtils.isAppGroup() && !CollectionUtils.isEmpty(grayTopics) && grayTopics.contains(topics[i])) {
                                    grayEnable = true;
                                    topics[i] = topics[i] + StressConstants.MQ_GRAY_SUFFIX;
                                }
                                log.info("register topic: {}", topics[i]);
                            }
                            if (grayEnable) {
                                /**
                                 * @see org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor#getEndpointGroupId(KafkaListener, String)
                                 * @see org.springframework.kafka.config.AbstractKafkaListenerContainerFactory#initializeContainer(AbstractMessageListenerContainer, KafkaListenerEndpoint)
                                 * @see org.springframework.kafka.listener.AbstractMessageListenerContainer#getGroupId()
                                 * @see org.springframework.kafka.listener.AbstractMessageListenerContainer#checkGroupId()
                                 */
                                String groupId = null;
                                if (StringUtils.hasText(kafkaListener.groupId())) {
                                    groupId = kafkaListener.groupId();
                                }
                                if (groupId == null && kafkaListener.idIsGroup() && StringUtils.hasText(kafkaListener.id())) {
                                    groupId = kafkaListener.id();
                                }
                                if (groupId == null && kafkaProperties != null && kafkaProperties.getConsumer() != null && StringUtils.hasText(kafkaProperties.getConsumer().getGroupId())) {
                                    groupId = kafkaProperties.getConsumer().getGroupId();
                                }
                                Assert.state(StringUtils.hasText(groupId),
                                        "No group.id found in consumer config, container properties, or @KafkaListener annotation; "
                                                + "a group.id is required when group management is used.");
                                memberValues.put("groupId", groupId + StressConstants.MQ_GRAY_SUFFIX);
                            }
                            log.info("register group: {}", kafkaListener.groupId());
                        }
                    }
                });
            }
        });
    }

    /**
     * @see org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization(Object, String)
     */
    @Override
    public void setEnvironment(Environment environment) {
        Binder binder = Binder.get(environment);
        binder.bind(KafkaConstants.PROPERTY_PREFIX, KafkaProperties.class).ifBound(b -> this.properties = b);
        binder.bind("spring.kafka", org.springframework.boot.autoconfigure.kafka.KafkaProperties.class).ifBound(b -> this.kafkaProperties = b);
    }
}