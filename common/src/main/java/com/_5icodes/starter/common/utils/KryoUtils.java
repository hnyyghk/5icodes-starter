package com._5icodes.starter.common.utils;

import com._5icodes.starter.common.utils.kryo.KryoFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;

@UtilityClass
public class KryoUtils {
    private final int INIT_BUFFER_SIZE = 512;

    private final KryoFactory KRYO_FACTORY = ServiceLoaderUtils.loadByOrder(KryoFactory.class);

    private final ThreadLocal<Object[]> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = KRYO_FACTORY.createKryo();
        Output output = new Output(INIT_BUFFER_SIZE, -1);
        WeakReference<Output> reference = new WeakReference<>(output);
        return new Object[]{kryo, reference};
    });

    public Kryo getKryo() {
        return (Kryo) KRYO_THREAD_LOCAL.get()[0];
    }

    public byte[] serialize(Object obj) {
        try {
            Object[] kryoAndOutput = KRYO_THREAD_LOCAL.get();
            Kryo kryo = (Kryo) kryoAndOutput[0];
            WeakReference<Output> reference = (WeakReference<Output>) kryoAndOutput[1];
            Output output = reference.get();
            if (output == null) {
                output = new Output(INIT_BUFFER_SIZE, -1);
                kryoAndOutput[1] = new WeakReference<>(output);
            }
            try {
                kryo.writeClassAndObject(output, obj);
                return output.toBytes();
            } finally {
                output.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException("kryo serialize error", e);
        }
    }

    public Object deserialize(byte[] buffer) {
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        Input input = new Input(in);
        Kryo kryo = (Kryo) KRYO_THREAD_LOCAL.get()[0];
        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        return kryo.readClassAndObject(input);
    }
}