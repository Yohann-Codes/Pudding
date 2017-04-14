package org.pudding.serialization.gson;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerType;

import java.io.UnsupportedEncodingException;

/**
 * Gson序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class GsonSerializer implements Serializer {
    private static final Logger logger = Logger.getLogger(GsonSerializer.class);

    private final Gson gson = new Gson();

    @Override
    public byte type() {
        return SerializerType.GSON.value();
    }

    @Override
    public <T> byte[] writeObject(T object) {
        String json = gson.toJson(object);
        try {
            return json.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn("GsonSerializer.writeObject()", e);
        }
        return null; // never get here
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        try {
            String json = new String(bytes, "utf-8");
            return gson.fromJson(json, clazz);
        } catch (UnsupportedEncodingException e) {
            logger.warn("GsonSerializer.readObject()", e);
        }
        return null; // never get here
    }
}
