package com._5icodes.starter.common.utils.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

public class DefaultKryoFactory implements KryoFactory {
    @Override
    public Kryo createKryo() {
        Kryo kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        kryo.setReferences(false);
        return kryo;
    }
}