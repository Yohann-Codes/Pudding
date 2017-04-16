package org.pudding.rpc.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.RpcConfig;

/**
 * Service Provider:
 * <p>
 * 1) connect to registry.
 * 2) start service.
 * 3) publish service.
 * 4) unpublish service.
 *
 * @author Yohann.
 */
public interface ServiceProvider {

    /**
     * Connect with registry_cluster (only one).
     * <p>
     * Notice:
     * You must call {@link RpcConfig#setRegistryAddress(String...)}
     * to configure the registry_cluster address before invoke this method. Otherwise, throw {@link IllegalStateException}.
     */
    ServiceProvider connectRegistry();

    /**
     * Connect with registry_cluster (only one).
     *
     * @param registryAddress
     */
    ServiceProvider connectRegistry(String... registryAddress);

    /**
     * Start a service.
     *
     * @param serviceMeta
     */
    ServiceProvider startService(ServiceMeta serviceMeta);

    /**
     * Start multiple service.
     *
     * @param serviceMeta
     */
    ServiceProvider startServices(ServiceMeta... serviceMeta);

    /**
     * Publish a service.
     * <p>
     * Notice:
     * You must start service before invoke this method. Otherwise, throw {@link IllegalStateException}.
     *
     * @param serviceMeta
     */
    ServiceProvider publicService(ServiceMeta serviceMeta);

    /**
     * Publish multiple service.
     * <p>
     * Notice:
     * You must start service before invoke this method. Otherwise, throw {@link IllegalStateException}.
     *
     * @param serviceMeta
     */
    ServiceProvider publicServices(ServiceMeta... serviceMeta);

    /**
     * Publish all service that has started.
     * <p>
     * Notice:
     * 1). You must start service before invoke this method. Otherwise, throw {@link IllegalStateException}.
     * 2). This method can be called only once. Otherwise, throw {@link IllegalStateException}.
     */
    ServiceProvider publishAllService();

    /**
     * Unpublish a service.
     *
     * @param serviceMeta
     */
    ServiceProvider unpulishService(ServiceMeta serviceMeta);

    /**
     * Unpublish multiple service.
     *
     * @param serviceMeta
     */
    ServiceProvider unpulishServices(ServiceMeta... serviceMeta);

    /**
     * Unpublish all service that has pushlished.
     */
    ServiceProvider unpublishAllService();

    /**
     * Stop the specified service.
     *
     * @param serviceMeta
     */
    ServiceProvider stopService(ServiceMeta serviceMeta);

    /**
     * Stop multiple service.
     *
     * @param serviceMeta
     */
    ServiceProvider stopServices(ServiceMeta... serviceMeta);

    /**
     * Stop all service that has started.
     */
    ServiceProvider stopAllService();

    /**
     * Return the number of worker thread.
     */
    int workers();

    /**
     * Shutdown the {@link ServiceProvider}.
     * <p>
     * Notice:
     * If you call the method to shudown the current {@link ServiceProvider}, you must new a {@link ServiceProvider}'s
     * instance before connect with registry_cluster or start a service. Otherwise, throw {@link IllegalStateException}.
     */
    void shutdown();
}