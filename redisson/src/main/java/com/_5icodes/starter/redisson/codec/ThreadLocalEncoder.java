package com._5icodes.starter.redisson.codec;

import com._5icodes.starter.common.utils.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.protocol.Encoder;
import org.springframework.core.codec.CodecException;

import java.io.IOException;

public class ThreadLocalEncoder implements Encoder {
    @Override
    public ByteBuf encode(Object in) throws IOException {
        Kryo kryo = KryoUtils.getKryo();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        try {
            ByteBufOutputStream out = new ByteBufOutputStream(buffer);
            Output output = new Output(out);
            kryo.writeClassAndObject(output, in);
            output.close();
            return out.buffer();
        } catch (Exception e) {
            buffer.release();
            throw new CodecException("kryo encode error", e);
        }
    }
}