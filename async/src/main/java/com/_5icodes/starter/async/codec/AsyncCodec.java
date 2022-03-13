package com._5icodes.starter.async.codec;

public interface AsyncCodec {
    byte[] encode(Object obj);

    Object decode(byte[] buffer);
}