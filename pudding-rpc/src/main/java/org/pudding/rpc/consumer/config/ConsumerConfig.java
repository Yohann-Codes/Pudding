package org.pudding.rpc.consumer.config;

import org.pudding.common.constant.LoadBalanceStrategy;
import org.pudding.common.constant.SerializerType;
import org.pudding.common.constant.Timeout;

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

    /** 序列化类型, 默认Java原生 */
    private byte serializerType = SerializerType.JAVA;

    /** 注册中心地址, 格式: host:port, 没有默认值需要配置 */
    private String registryAddress = "";

    /** 负载均衡策略, 默认随机 */
    private byte loadBalanceStrategy = LoadBalanceStrategy.RANDOM;

    /** 服务订阅超时时间, 默认: 15s */
    private int subscribeTimeout = Timeout.SUBSCRIBE_TIMEOUT;

    /** 服务调用超时时间, 默认: 10s */
    private int invokeTimeout = Timeout.INVOKE_TIMEOUT;

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

    public static void loadBalanceStrategy(byte loadBalanceStrategy) {
        CONSUMER_CONFIG.loadBalanceStrategy = loadBalanceStrategy;
    }

    public static byte loadBalanceStrategy() {
        return CONSUMER_CONFIG.loadBalanceStrategy;
    }

    public static void subscribeTimeout(int subscribeTimeout) {
        CONSUMER_CONFIG.subscribeTimeout = subscribeTimeout;
    }

    public static int subscribeTimeout() {
        return CONSUMER_CONFIG.subscribeTimeout;
    }

    public static void invokeTimeout(int invokeTimeout) {
        CONSUMER_CONFIG.invokeTimeout = invokeTimeout;
    }

    public static int invokeTimeout() {
        return CONSUMER_CONFIG.invokeTimeout;
    }
}
