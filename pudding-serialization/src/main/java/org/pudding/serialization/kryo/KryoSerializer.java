package org.pudding.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.log4j.Logger;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = Logger.getLogger(KryoSerializer.class);

    private final Kryo kryo = new Kryo(); // 经测试构造此对象非常耗时

    @Override
    public byte type() {
        return SerializerType.KRYO.value();
    }

    @Override
    public <T> byte[] writeObject(T object) {
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        Input input = new Input(new ByteArrayInputStream(bytes));
        T object = kryo.readObject(input, clazz);
        input.close();
        return object;
    }
}
