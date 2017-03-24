package org.pudding.serialization.api;

/**
 * The Serialization interface.
 * SerializerImpl是基于SPI加载的.
 *
 * @author Yohann.
 */
public interface Serializer {

    /**
     * @return The serialization type.
     */
    byte type();

    /**
     * Serialize.
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] writeObject(T object);

    /**
     * Deserialize.
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T readObject(byte[] bytes, Class<T> clazz);
}
