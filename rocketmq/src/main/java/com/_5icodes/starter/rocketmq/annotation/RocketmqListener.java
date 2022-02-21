package com._5icodes.starter.rocketmq.annotation;

import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Component
public @interface RocketmqListener {
    ConsumeFromWhere fromWhere() default ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * 分组订阅
     */
    boolean grayEnable() default false;

    String group();

    /**
     * Batch pull size
     */
    int batch() default 1;

    /**
     * Minimum consumer thread number，默认值Runtime.getRuntime().availableProcessors() * 2
     */
    int minThread() default Integer.MAX_VALUE;

    /**
     * Maximum consumer thread number，默认值Runtime.getRuntime().availableProcessors() * 2
     */
    int maxThread() default Integer.MAX_VALUE;

    TopicSpec[] topics();
}