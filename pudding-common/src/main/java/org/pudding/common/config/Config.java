package org.pudding.common.config;

/**
 * Pudding配置常量.
 *
 * @author Yohann.
 */
public class Config {
    /**
     * 序列化类型.
     */
    static class Serializer {
        public static final byte JAVA = 0x01; // Java
        public static final byte KRYO = 0x02; // Kryo
        public static final byte HESSIAN = 0x03; // Hessian
        public static final byte GSON = 0x04; // Gson
    }
}
