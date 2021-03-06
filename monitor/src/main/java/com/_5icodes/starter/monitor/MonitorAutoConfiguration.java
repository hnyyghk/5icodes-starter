package com._5icodes.starter.monitor;

import brave.propagation.CurrentTraceContext;
import com._5icodes.starter.kafka.KafkaAutoConfiguration;
import com._5icodes.starter.monitor.common.CommonMetaInfoProvider;
import com._5icodes.starter.monitor.meta.KafkaMetaInfoSender;
import com._5icodes.starter.monitor.meta.MetaInfoProvider;
import com._5icodes.starter.monitor.meta.MetaInfoSender;
import com._5icodes.starter.monitor.meta.MetaInfoSenderApplicationEventAdapter;
import com._5icodes.starter.monitor.spi.ModulesMetaInfoProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.List;

@Configuration
@EnableConfigurationProperties(MonitorKafkaProperties.class)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
public class MonitorAutoConfiguration {
    @Bean
    public MetaInfoSenderApplicationEventAdapter metaInfoSenderApplicationEventAdapter(@Autowired(required = false) List<MetaInfoProvider> metaInfoProviders, MetaInfoSender metaInfoSender) {
        return new MetaInfoSenderApplicationEventAdapter(metaInfoProviders, metaInfoSender);
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaInfoSender kafkaMetaInfoSender(MonitorKafkaTemplate monitorKafkaTemplate, MonitorKafkaProperties monitorKafkaProperties) {
        return new KafkaMetaInfoSender(monitorKafkaTemplate, monitorKafkaProperties);
    }

    @Bean
    public ModulesMetaInfoProvider modulesMetaInfoProvider() {
        return new ModulesMetaInfoProvider();
    }

    @Bean
    public CommonMetaInfoProvider commonMetaInfoProvider() {
        return new CommonMetaInfoProvider();
    }

    @Bean
    public ExceptionReport exceptionReport(MonitorKafkaTemplate monitorKafkaTemplate, CurrentTraceContext currentTraceContext, MonitorKafkaProperties monitorKafkaProperties) {
        return new ExceptionReport(monitorKafkaTemplate, currentTraceContext, monitorKafkaProperties);
    }

    @Bean
    public MonitorKafkaTemplate monitorKafkaTemplate(ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers,
                                                     ProducerListener<Object, Object> kafkaProducerListener,
                                                     ObjectProvider<RecordMessageConverter> messageConverter,
                                                     MonitorKafkaProperties monitorProperties) {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers(monitorProperties.getBootstrapServers());
        KafkaProperties.Producer producer = kafkaProperties.getProducer();
        BeanCopier beanCopier = BeanCopier.create(MonitorKafkaProperties.class, KafkaProperties.Producer.class, false);
        beanCopier.copy(monitorProperties, producer, null);
        producer.getProperties().putAll(monitorProperties.getProperties());
        /**
         * @see org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration#kafkaProducerFactory(ObjectProvider)
         */
        DefaultKafkaProducerFactory<Object, Object> factory = new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties());
        String transactionIdPrefix = kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        /**
         * @see org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration#kafkaTemplate(ProducerFactory, ProducerListener, ObjectProvider)
         */
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(factory);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(kafkaProperties.getTemplate().getDefaultTopic());
        return new MonitorKafkaTemplate(kafkaTemplate, monitorProperties);
    }
}