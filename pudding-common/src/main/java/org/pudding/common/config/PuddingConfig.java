package org.pudding.common.config;

/**
 * Pudding配置实现.
 *
 * @author Yohann.
 */
public class PuddingConfig implements IPuddingConfig {

    /** 序列化类型默认为Java原生 */
    public static byte serializerType = Config.Serializer.JAVA;

    @Override
    public void serializerType(byte type) {
        serializerType = type;
    }
}
