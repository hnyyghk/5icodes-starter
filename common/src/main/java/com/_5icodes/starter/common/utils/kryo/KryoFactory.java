package com._5icodes.starter.common.utils.kryo;

import com.esotericsoftware.kryo.Kryo;

public interface KryoFactory {
    Kryo createKryo();
}