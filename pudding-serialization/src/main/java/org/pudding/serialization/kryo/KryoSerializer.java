package org.pudding.serialization.kryo;

import org.apache.log4j.Logger;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerType;

/**
 * Kryo序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = Logger.getLogger(KryoSerializer.class);

    @Override
    public byte type() {
        return SerializerType.KRYO.value();
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
