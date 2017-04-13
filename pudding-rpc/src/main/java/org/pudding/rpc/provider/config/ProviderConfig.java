package org.pudding.rpc.provider.config;

import org.pudding.common.constant.SerializerType;
import org.pudding.common.constant.Timeout;
import org.pudding.common.utils.Lists;

import java.util.List;

/**
 * Configure the {@link org.pudding.rpc.provider.ServiceProvider}.
 *
 * @author Yohann.
 */
public class ProviderConfig {

    private static final ProviderConfig PROVIDER_CONFIG;

    static {
        PROVIDER_CONFIG = new ProviderConfig();
    }


    /** The serializer type, default: Java */
    private byte serializerType = SerializerType.JAVA;

    /** The registry address, single address format: "host:port" */
    private String[] registryAddress = null;

    /** The number of worker thread, default: 2*CPU */
    private int workers = Runtime.getRuntime().availableProcessors() * 2;

    /** The deadline of pushlishing service, default: 15s */
    private int publishTimeout = Timeout.PUBLISH_TIMEOUT;


    /**
     * Set the serializer type.
     */
    public static void setSerializerType(byte serializerType) {
        PROVIDER_CONFIG.serializerType = serializerType;
    }

    /**
     * Return the serializer type.
     */
    public static byte getSerializerType() {
        return PROVIDER_CONFIG.serializerType;
    }

    /**
     * Set the registry address, there may be multiple.
     *
     * @param registryAddress "host:port"
     */
    public static void setRegistryAddress(String... registryAddress) {
        List<String> addrs = Lists.newArrayList();
        for (String addr : registryAddress) {
            addrs.add(addr);
        }
        PROVIDER_CONFIG.registryAddress = (String[]) addrs.toArray();
    }

    /**
     * Return the registry address.
     */
    public static String[] getRegistryAddress() {
        return PROVIDER_CONFIG.registryAddress;
    }

    /**
     * Set the number of worker thread.
     */
    public static void setWorkers(int workers) {
        PROVIDER_CONFIG.workers = workers;
    }

    /**
     * Return the number of worker thread.
     */
    public static int getWorkers() {
        return PROVIDER_CONFIG.workers;
    }

    /**
     * Set the deadline of pushlishing service.
     */
    public static void setPublishTimeout(int publishTimeout) {
        PROVIDER_CONFIG.publishTimeout = publishTimeout;
    }

    /**
     * Return the deadline of pusglishling service.
     */
    public static int getPublishTimeout() {
        return PROVIDER_CONFIG.publishTimeout;
    }
}
