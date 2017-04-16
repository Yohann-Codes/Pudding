package org.pudding.registry.config;

import org.pudding.common.constant.SerializerType;
import org.pudding.common.utils.Lists;

import java.util.List;

/**
 * Congfigure {@link org.pudding.registry.ServiceRegistry}.
 *
 * @author Yohann.
 */
public class RegistryConfig {

    private static final RegistryConfig REGISTRY_CONFIG;

    static {
        REGISTRY_CONFIG = new RegistryConfig();
    }


    /** The serialization type, default: Java */
    private byte serializationType = SerializerType.JAVA;

    /** The port of listening, default: 20000 */
    private int port = 20000;

    /** The cluster address, single address format: "host:port" */
    private String[] clusterAddress = null;

    /** The number of worker thread, default: 2*CPU */
    private int workers = Runtime.getRuntime().availableProcessors() * 2;


    /**
     * Set serialization type.
     */
    public static void setSerializationType(byte serializationType) {
        REGISTRY_CONFIG.serializationType = serializationType;
    }

    /**
     * Return serialization type.
     */
    public static byte getSerializationType() {
        return REGISTRY_CONFIG.serializationType;
    }

    /**
     * Set port of listening.
     */
    public static void setPort(int port) {
        REGISTRY_CONFIG.port = port;
    }

    /**
     * Return port of listening.
     */
    public static int getPort() {
        return REGISTRY_CONFIG.port;
    }

    /**
     * Set address of registry_cluster cluster.
     */
    public static void setClusterAddress(String... clusterAddress) {
        REGISTRY_CONFIG.clusterAddress = clusterAddress;
    }

    /**
     * Return address of registry_cluster cluster.
     */
    public static String[] getClusterAddress() {
        return REGISTRY_CONFIG.clusterAddress;
    }

    /**
     * Set the thread number of workers.
     */
    public static void setWorkers(int workers) {
        REGISTRY_CONFIG.workers = workers;
    }

    /**
     * Return the thread number of workers.
     */
    public static int getWorkers() {
        return REGISTRY_CONFIG.workers;
    }
}
