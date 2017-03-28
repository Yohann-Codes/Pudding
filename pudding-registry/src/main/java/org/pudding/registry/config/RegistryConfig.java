package org.pudding.registry.config;

import org.pudding.common.constant.SerializerType;

/**
 * 注册中心配置.
 *
 * @author Yohann.
 */
public class RegistryConfig {

    // Registry唯一的配置实例
    private static final RegistryConfig REGISTRY_CONFIG;

    static {
        REGISTRY_CONFIG = new RegistryConfig();
    }


    /** 序列化类型, 默认: Java原生 */
    private byte serializerType = SerializerType.JAVA;

    /** 绑定端口, 默认: 20000 */
    private int port = 20000;

    /** 工作线程数量, 默认: 2*CPU */
    private int nWorkers = Runtime.getRuntime().availableProcessors() * 2;


    public static void serializerType(byte serializerType) {
        REGISTRY_CONFIG.serializerType = serializerType;
    }

    public static byte serializerType() {
        return REGISTRY_CONFIG.serializerType;
    }

    public static void port(int port) {
        REGISTRY_CONFIG.port = port;
    }

    public static int port() {
        return REGISTRY_CONFIG.port;
    }

    public static void nWorkers(int nWorkers) {
        REGISTRY_CONFIG.nWorkers = nWorkers;
    }

    public static int nWorkers() {
        return REGISTRY_CONFIG.nWorkers;
    }
}
