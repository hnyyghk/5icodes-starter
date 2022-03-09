package com._5icodes.starter.rocketmq;

import brave.Tracing;
import com._5icodes.starter.common.Initial;
import com._5icodes.starter.monitor.ExceptionReport;
import com._5icodes.starter.rocketmq.consumer.RocketmqConsumerContainer;
import com._5icodes.starter.rocketmq.interceptor.ErrorReportInterceptor;
import com._5icodes.starter.rocketmq.interceptor.MessageInterceptor;
import com._5icodes.starter.rocketmq.interceptor.MessageInterceptorList;
import com._5icodes.starter.rocketmq.producer.RocketmqProducerAspect;
import com._5icodes.starter.rocketmq.producer.RocketmqProducerContainer;
import com._5icodes.starter.rocketmq.sleuth.RocketmqMessagePropagation;
import com._5icodes.starter.rocketmq.sleuth.TraceMessageInterceptor;
import com._5icodes.starter.rocketmq.trace.TraceTestMessageInterceptor;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(RocketmqProperties.class)
public class RocketmqAutoConfiguration {
    @Bean
    public RocketmqConsumerContainer rocketmqConsumerContainer(RocketmqProperties properties, MessageInterceptorList interceptorList) {
        return new RocketmqConsumerContainer(properties, interceptorList);
    }

    @Bean
    public DefaultMQProducer defaultMqProducer(RocketmqProperties properties, @Autowired(required = false) Initial<DefaultMQProducer> producerInitial) {
        DefaultMQProducer producer = new DefaultMQProducer(properties.getGroup());
        producer.setNamesrvAddr(properties.getNameSrvAddr());
        producer.setSendMsgTimeout(properties.getTimeout());
        producer.setDefaultTopicQueueNums(properties.getQueueNums());
        producer.setCompressMsgBodyOverHowmuch(properties.getCompressMsgBodySize());
        producer.setRetryTimesWhenSendFailed(properties.getRetryTimes());
        producer.setRetryTimesWhenSendAsyncFailed(properties.getRetryTimesAsync());
        producer.setRetryAnotherBrokerWhenNotStoreOK(properties.isRetrySendMsg());
        producer.setMaxMessageSize(properties.getMaxMessageSize());
        if (producerInitial != null) {
            producerInitial.init(producer);
        }
        return producer;
    }

    @Bean
    public RocketmqProducerContainer rocketmqProducerContainer(DefaultMQProducer defaultMqProducer) {
        return new RocketmqProducerContainer(defaultMqProducer);
    }

    @Bean
    public RocketmqMessagePropagation rocketmqMessagePropagation() {
        return new RocketmqMessagePropagation();
    }

    @Bean
    public TraceMessageInterceptor traceMessageInterceptor(Tracing tracing, RocketmqMessagePropagation messagePropagation) {
        return new TraceMessageInterceptor(tracing, messagePropagation, messagePropagation);
    }

    @Bean
    public ErrorReportInterceptor errorReportInterceptor(ExceptionReport exceptionReport) {
        return new ErrorReportInterceptor(exceptionReport);
    }

    @Bean
    public TraceTestMessageInterceptor traceTestMessageInterceptor(RocketmqProperties properties) {
        return new TraceTestMessageInterceptor(properties);
    }

    @Bean
    public MessageInterceptorList messageInterceptorList(@Autowired(required = false) List<MessageInterceptor> interceptors) {
        return new MessageInterceptorList(interceptors);
    }

    @Bean
    public RocketmqProducerAspect rocketmqProducerAspect(MessageInterceptorList messageInterceptorList) {
        return new RocketmqProducerAspect(messageInterceptorList);
    }
}