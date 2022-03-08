package com._5icodes.starter.rocketmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = RocketmqConstants.PROPERTY_PREFIX)
public class RocketmqProperties {
    /**
     * Producer group conceptually aggregates all producer instances of exactly same role
     */
    private String group;

    /**
     * rocketmq.namesrv.addr
     */
    private String nameSrvAddr;

    /**
     * Millis of send message timeout.
     */
    private int timeout = 3000;

    /**
     * Number of queues to create per default topic.
     */
    private int queueNums = 4;

    /**
     * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
     */
    private int compressMsgBodySize = 1024 * 4;

    /**
     * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int retryTimes = 2;

    /**
     * Maximum number of retry to perform internally before claiming sending failure in asynchronous mode.
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int retryTimesAsync = 2;

    /**
     * Indicate whether to retry another broker on sending failure internally.
     */
    private boolean retrySendMsg = false;

    /**
     * Maximum allowed message size in bytes.
     */
    private int maxMessageSize = 1024 * 1024 * 4;

    private List<String> grayTopics;

    private Map<String, Consumer> consumers = new HashMap<>();

    @Data
    public static class Consumer {
        private String group;

        /**
         * Batch pull size
         */
        private Integer batch;

        /**
         * Minimum consumer thread number，默认值Runtime.getRuntime().availableProcessors() * 2
         */
        private Integer minThread;

        /**
         * Maximum consumer thread number，默认值Runtime.getRuntime().availableProcessors() * 2
         */
        private Integer maxThread;

        private List<TopicSpec> topics;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopicSpec {
        private String topic;

        private String tags;

        private String sql;
    }
}