package org.pudding.serialization.java;

import org.apache.log4j.Logger;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerType;

import java.io.*;

/**
 * Java原生序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class JavaSerializer implements Serializer {
    private static final Logger logger = Logger.getLogger(JavaSerializer.class);

    @Override
    public byte type() {
        return SerializerType.JAVA.value();
    }

    @Override
    public <T> byte[] writeObject(T object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(bos);
            output.writeObject(object);
            output.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            logger.warn("JavaSerializer.writeObject()", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignored) {
                    // ignore
                }
            }
        }
        return null; // never get here
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(bis);
            Object object = input.readObject();
            return clazz.cast(object);
        } catch (Exception e) {
            logger.warn("JavaSerializer.readObject()", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                    // ignore
                }
            }
        }
        return null; // never get here
    }
}
