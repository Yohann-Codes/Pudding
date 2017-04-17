package org.pudding.rpc.consumer;

import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.RpcConfig;

/**
 * Service consumer.
 * <p>
 * 1) connect to registry.
 * 2) subscribe service.
 *
 * @author Yohann.
 */
public interface ServiceConsumer {

    /**
     * Connect with registry_cluster (only one).
     * <p>
     * Notice:
     * You must call {@link RpcConfig#setRegistryAddress(String...)}
     * to configure the registry_cluster address before invoke this method. Otherwise, throw {@link IllegalStateException}.
     */
    ServiceConsumer connectRegistry();

    /**
     * Connect with registry_cluster (only one).
     *
     * @param registryAddress
     */
    ServiceConsumer connectRegistry(String... registryAddress);

    /**
     * Subscribe the specific service.
     *
     * @param serviceMeta
     */
    ServiceConsumer subscribeService(ServiceMeta serviceMeta);

    /**
     * Subscribe multiple services.
     *
     * @param serviceMeta
     */
    ServiceConsumer subscribeServices(ServiceMeta... serviceMeta);

    /**
     * Return the number of worker thread.
     */
    int workers();

    /**
     * Shutdown the {@link ServiceConsumer}.
     */
    void shutdown();
}
