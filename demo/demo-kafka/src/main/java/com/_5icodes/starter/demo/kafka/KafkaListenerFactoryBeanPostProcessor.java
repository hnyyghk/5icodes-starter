package com._5icodes.starter.demo.kafka;

import com._5icodes.starter.common.utils.AnnotationChangeUtils;
import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.kafka.KafkaConstants;
import com._5icodes.starter.kafka.KafkaProperties;
import com._5icodes.starter.stress.StressConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 弃用旧的覆盖注解的方式，归档
 */
@Slf4j
public class KafkaListenerFactoryBeanPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {
    private KafkaProperties properties;

    @SneakyThrows
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<String> packageNames = AutoConfigurationPackages.get(beanFactory);
        for (String packageName : packageNames) {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(packageName) // 指定路径URL
                    .addScanners(new SubTypesScanner()) // 添加子类扫描工具
                    .addScanners(new FieldAnnotationsScanner()) // 添加属性注解扫描工具
                    .addScanners(new MethodAnnotationsScanner()) // 添加方法注解扫描工具
                    .addScanners(new MethodParameterScanner()) // 添加方法参数扫描工具
            );
            //@KafkaListeners({@KafkaListener(topics = "TEST_TOPIC"), @KafkaListener(topics = "TEST_TOPIC_2")})
            //或
            //@KafkaListener(topics = "TEST_TOPIC")
            //@KafkaListener(topics = "TEST_TOPIC_2")
            //或
            //@KafkaListener(topics = "TEST_TOPIC")
            Set<Method> methodSet = reflections.getMethodsAnnotatedWith(KafkaListener.class);
//            Set<Method> methodSet = reflections.getMethodsAnnotatedWith(KafkaListeners.class);
            if (!CollectionUtils.isEmpty(methodSet)) {
                for (Method method : methodSet) {
                    List<KafkaListener> grayAnnotations = new ArrayList<>();
                    //JDK8的重复注解特性@Repeatable，通过getAnnotationsByType()来返回重复注解的类型
                    KafkaListener[] annotations = method.getAnnotationsByType(KafkaListener.class);
                    if (annotations == null || annotations.length == 0) {
                        continue;
                    }
                    for (KafkaListener annotation : annotations) {
                        KafkaListener grayAnnotation = new KafkaListener() {
                            @Override
                            public Class<? extends Annotation> annotationType() {
                                return KafkaListener.class;
                            }

                            @Override
                            public String id() {
                                return annotation.id();
                            }

                            @Override
                            public String containerFactory() {
                                return annotation.containerFactory();
                            }

                            @Override
                            public String[] topics() {
                                String[] topics = annotation.topics();
                                List<String> grayTopics = properties.getGrayTopics();
                                for (int i = 0; i < topics.length; i++) {
                                    if (GrayUtils.isAppGroup() && !CollectionUtils.isEmpty(grayTopics) && grayTopics.contains(topics[i])) {
                                        topics[i] = topics[i] + StressConstants.MQ_GRAY_SUFFIX;
                                    }
                                }
                                return topics;
                            }

                            @Override
                            public String topicPattern() {
                                return annotation.topicPattern();
                            }

                            @Override
                            public TopicPartition[] topicPartitions() {
                                return annotation.topicPartitions();
                            }

                            @Override
                            public String containerGroup() {
                                return annotation.containerGroup();
                            }

                            @Override
                            public String errorHandler() {
                                return annotation.errorHandler();
                            }

                            @Override
                            public String groupId() {
                                return annotation.groupId();
                            }

                            @Override
                            public boolean idIsGroup() {
                                return annotation.idIsGroup();
                            }

                            @Override
                            public String clientIdPrefix() {
                                return annotation.clientIdPrefix();
                            }

                            @Override
                            public String beanRef() {
                                return annotation.beanRef();
                            }

                            @Override
                            public String concurrency() {
                                return annotation.concurrency();
                            }

                            @Override
                            public String autoStartup() {
                                return annotation.autoStartup();
                            }

                            @Override
                            public String[] properties() {
                                return annotation.properties();
                            }

                            @Override
                            public boolean splitIterables() {
                                return annotation.splitIterables();
                            }

                            @Override
                            public String contentTypeConverter() {
                                return annotation.contentTypeConverter();
                            }

                            @Override
                            public String batch() {
                                return annotation.batch();
                            }
                        };
                        grayAnnotations.add(grayAnnotation);
                    }
                    AnnotationChangeUtils.removeAnnotation(method, KafkaListener.class);
                    AnnotationChangeUtils.removeAnnotation(method, KafkaListeners.class);
                    KafkaListeners kafkaListeners = new KafkaListeners() {
                        @Override
                        public Class<? extends Annotation> annotationType() {
                            return KafkaListeners.class;
                        }

                        @Override
                        public KafkaListener[] value() {
                            return grayAnnotations.toArray(new KafkaListener[0]);
                        }
                    };
                    AnnotationChangeUtils.addAnnotation(method, KafkaListeners.class, kafkaListeners);
                }
            }
        }
    }

    /**
     * @see org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization(Object, String)
     */
    @Override
    public void setEnvironment(Environment environment) {
        Binder binder = Binder.get(environment);
        binder.bind(KafkaConstants.PROPERTY_PREFIX, KafkaProperties.class).ifBound(b -> this.properties = b);
    }
}