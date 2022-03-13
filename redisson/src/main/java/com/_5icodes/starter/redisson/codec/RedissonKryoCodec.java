package com._5icodes.starter.redisson.codec;

import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

public class RedissonKryoCodec extends BaseCodec {
    private static final ThreadLocalDecoder DECODER = new ThreadLocalDecoder();

    private static final ThreadLocalEncoder ENCODER = new ThreadLocalEncoder();

    @Override
    public Decoder<Object> getValueDecoder() {
        return DECODER;
    }

    @Override
    public Encoder getValueEncoder() {
        return ENCODER;
    }
}