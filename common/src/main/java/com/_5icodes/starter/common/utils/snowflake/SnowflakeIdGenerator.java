package com._5icodes.starter.common.utils.snowflake;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
public class SnowflakeIdGenerator {
    /**
     * 开始时间戳
     */
    private final long epoch;
    /**
     * 业务id所占的位数
     */
    private final int customIdBits;
    /**
     * 序列id所占的位数
     */
    private final int sequenceBits;
    /**
     * 业务id
     */
    private final long customId;
    /**
     * 序列id
     */
    private long sequence = 0;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = 0;

    private static final int CUSTOM_ID_BITS = 12;
    private static final int SEQUENCE_BITS = 12;
    private static final long EPOCH = 1483200000000L;

    public SnowflakeIdGenerator(long customId) {
        this(customId, EPOCH, CUSTOM_ID_BITS, SEQUENCE_BITS);
    }

    public SnowflakeIdGenerator(long customId, long epoch, int customIdBits, int sequenceBits) {
        log.info("initializing snowflakeIdGenerator, epoch start with: {}, available until: {}, customId range: 0 ~ {}, current customId: {}, low {} bit customId: {}, sequence range: 0 ~ {}, support generate {} ids per second single machine.",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(epoch),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(epoch + ~(-1L << 63 - customIdBits - sequenceBits)),
                ~(-1L << customIdBits),
                customId,
                customIdBits,
                customId & ~(-1L << customIdBits),
                ~(-1L << sequenceBits),
                (1L << sequenceBits) * 1000);
        //业务id取低customIdBits位
        this.customId = customId & ~(-1L << customIdBits);
        this.epoch = epoch;
        this.customIdBits = customIdBits;
        this.sequenceBits = sequenceBits;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp == lastTimestamp) {
            //序列id取低sequenceBits位
            sequence = (sequence + 1) & ~(-1L << sequenceBits);
            if (sequence == 0) {
                //当生成序列超出毫秒内序列最大值时会阻塞到下一个毫秒生成
                timestamp = tilNextMillis();
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成63位的id
        return ((timestamp - epoch) << (sequenceBits + customIdBits))
                //时间戳向左移sequenceBits+customIdBits位
                | (customId << sequenceBits)
                //业务id向左移sequenceBits位
                | sequence;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long tilNextMillis() {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
}