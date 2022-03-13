package com._5icodes.starter.async.codec;

import com._5icodes.starter.common.utils.KryoUtils;

public class AsyncKryoCodec implements AsyncCodec {
    @Override
    public byte[] encode(Object obj) {
        return KryoUtils.serialize(obj);
    }

    @Override
    public Object decode(byte[] buffer) {
        return KryoUtils.deserialize(buffer);
    }
}