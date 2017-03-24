package api;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Holds all serializers.
 *
 * @author Yohann.
 */
public class SerializerFactory {

    private static Map<Byte, Serializer> serializerMap = new HashMap<>();

    static {
        ServiceLoader<Serializer> serializers = ServiceLoader.load(Serializer.class);
        for (Serializer s : serializers) {
            serializerMap.put(s.type(), s);
        }
    }

    public static Serializer getSerializer(byte type) {
        Serializer serializer = serializerMap.get(type);

        if (serializer == null) {
            throw new NullPointerException("unsupported serializerImpl with type: " + type);
        }

        return serializer;
    }
}
