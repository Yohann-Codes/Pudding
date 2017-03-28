package org.pudding.rpc.provider.config;

import org.pudding.common.constant.SerializerType;

/**
 * 服务提供者配置.
 *
 * @author Yohann.
 */
public class ProviderConfig {

    // Provider唯一的配置实例
    private static final ProviderConfig PROVIDER_CONFIG;

    static {
        PROVIDER_CONFIG = new ProviderConfig();
    }


    /** 序列化类型默认为Java原生 */
    private byte serializerType = SerializerType.JAVA;

    /** 注册中心地址, 格式: host:port */
    private String registryAddress = "";


    public static void serializerType(byte serializerType) {
        PROVIDER_CONFIG.serializerType = serializerType;
    }

    public static byte serializerType() {
        return PROVIDER_CONFIG.serializerType;
    }

    public static void registryAddress(String registryAddress) {
        PROVIDER_CONFIG.registryAddress = registryAddress;
    }

    public static String registryAddress() {
        return PROVIDER_CONFIG.registryAddress;
    }
}
