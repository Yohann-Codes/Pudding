package org.pudding.serialization.protostuff;

import org.apache.log4j.Logger;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerType;

/**
 * ProtoStuff序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class ProtoStuffSerializer implements Serializer {
    private static final Logger logger = Logger.getLogger(ProtoStuffSerializer.class);

    @Override
    public byte type() {
        return SerializerType.PROTOSTUFF.value();
    }

    @Override
    public <T> byte[] writeObject(T object) {
        return new byte[0];
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
