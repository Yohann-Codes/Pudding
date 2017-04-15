package org.pudding.rpc;

import org.pudding.common.constant.SerializerType;
import org.pudding.common.constant.Timeout;
import org.pudding.common.utils.Lists;

import java.util.List;

/**
 * Configure the {@link org.pudding.rpc.provider.ServiceProvider} and {@link org.pudding.rpc.consumer.ServiceConsumer}.
 *
 * @author Yohann.
 */
public class RpcConfig {

    private static final RpcConfig RPC_CONFIG;

    static {
        RPC_CONFIG = new RpcConfig();
    }


    /** The serialization type, default: Java */
    private byte serializationType = SerializerType.JAVA;

    /** The registry_cluster address, single address format: "host:port" */
    private String[] registryAddress = null;

    /** The number of worker thread, default: 2*CPU */
    private int workers = Runtime.getRuntime().availableProcessors() * 2;

    /** The deadline of pushlishing service, default: 15s */
    private int publishTimeout = Timeout.PUBLISH_TIMEOUT;


    /**
     * Set the serialization type.
     */
    public static void setSerializationType(byte serializationType) {
        RPC_CONFIG.serializationType = serializationType;
    }

    /**
     * Return the serialization type.
     */
    public static byte getSerializationType() {
        return RPC_CONFIG.serializationType;
    }

    /**
     * Set the registry_cluster address, there may be multiple.
     *
     * @param registryAddress "host:port"
     */
    public static void setRegistryAddress(String... registryAddress) {
        List<String> addrs = Lists.newArrayList();
        for (String addr : registryAddress) {
            addrs.add(addr);
        }
        registryAddress = new String[addrs.size()];
        for (int i = 0; i < registryAddress.length; i++) {
            registryAddress[i] = addrs.get(i);
        }
        RPC_CONFIG.registryAddress = registryAddress;
    }

    /**
     * Return the registry_cluster address.
     */
    public static String[] getRegistryAddress() {
        return RPC_CONFIG.registryAddress;
    }

    /**
     * Set the number of worker thread.
     */
    public static void setWorkers(int workers) {
        RPC_CONFIG.workers = workers;
    }

    /**
     * Return the number of worker thread.
     */
    public static int getWorkers() {
        return RPC_CONFIG.workers;
    }

    /**
     * Set the deadline of pushlishing service.
     */
    public static void setPublishTimeout(int publishTimeout) {
        RPC_CONFIG.publishTimeout = publishTimeout;
    }

    /**
     * Return the deadline of pusglishling service.
     */
    public static int getPublishTimeout() {
        return RPC_CONFIG.publishTimeout;
    }
}
