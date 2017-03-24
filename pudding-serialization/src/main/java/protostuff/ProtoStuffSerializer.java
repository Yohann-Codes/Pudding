package protostuff;

import api.Serializer;

/**
 * ProtoStuff序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class ProtoStuffSerializer implements Serializer {
    @Override
    public byte type() {
        return 0;
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
