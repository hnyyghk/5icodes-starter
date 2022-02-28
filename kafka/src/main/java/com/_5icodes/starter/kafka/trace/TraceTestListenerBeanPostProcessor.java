package com._5icodes.starter.kafka.trace;

import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.common.utils.AnnotationChangeUtils;
import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.kafka.KafkaProperties;
import com._5icodes.starter.stress.StressConstants;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TraceTestListenerBeanPostProcessor implements BeanFactoryPostProcessor {
    private final CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider;
    private final KafkaProperties properties;

    public TraceTestListenerBeanPostProcessor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider, KafkaProperties properties) {
        this.metadataReaderFactoryProvider = metadataReaderFactoryProvider;
        this.properties = properties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
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
                ReflectionUtils.doWithLocalMethods(aClass, method -> {
                    List<KafkaListener> grayAnnotations = new ArrayList<>();
                    //JDK8的重复注解特性@Repeatable，通过getAnnotationsByType()来返回重复注解的类型
                    KafkaListener[] annotations = method.getAnnotationsByType(KafkaListener.class);
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
                });
            }
        });
    }
}