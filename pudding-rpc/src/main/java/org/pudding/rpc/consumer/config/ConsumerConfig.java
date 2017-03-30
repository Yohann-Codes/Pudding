package org.pudding.rpc.consumer.config;

import org.pudding.common.constant.SerializerType;

/**
 * 服务消费者配置.
 *
 * @author Yohann.
 */
public class ConsumerConfig {

    // Consumer唯一的配置实例
    private static final ConsumerConfig CONSUMER_CONFIG;

    static {
        CONSUMER_CONFIG = new ConsumerConfig();
    }


    /** 序列化类型默认为Java原生 */
    private byte serializerType = SerializerType.JAVA;

    /** 注册中心地址, 格式: host:port */
    private String registryAddress = "";


    public static void serializerType(byte serializerType) {
        CONSUMER_CONFIG.serializerType = serializerType;
    }

    public static byte serializerType() {
        return CONSUMER_CONFIG.serializerType;
    }

    public static void registryAddress(String registryAddress) {
        CONSUMER_CONFIG.registryAddress = registryAddress;
    }

    public static String registryAddress() {
        return CONSUMER_CONFIG.registryAddress;
    }
}
