package com._5icodes.starter.redisson.codec;

import com._5icodes.starter.common.utils.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.springframework.core.codec.CodecException;

import java.io.IOException;

public class ThreadLocalDecoder implements Decoder<Object> {
    @Override
    public Object decode(ByteBuf buf, State state) throws IOException {
        Kryo kryo = KryoUtils.getKryo();
        try {
            return kryo.readClassAndObject(new Input(new ByteBufInputStream(buf)));
        } catch (Exception e) {
            throw new CodecException("kryo decode error", e);
        }
    }
}