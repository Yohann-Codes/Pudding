package org.pudding.rpc.provider;

import org.pudding.common.model.ServiceMeta;

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
     * Connect to registry (only one).
     * <p>
     * Notice:
     * You must call {@link org.pudding.rpc.provider.config.ProviderConfig#setRegistryAddress(String...)}
     * to configure the registry address before invoke this method. Otherwise, throw {@link IllegalStateException}.
     */
    ServiceProvider connectRegistry();

    /**
     * Connect to registry (only one).
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
     *
     * Notice:
     * You must start service before invoke this method. Otherwise, throw {@link IllegalStateException}.
     *
     * @param serviceMeta
     */
    ServiceProvider publicService(ServiceMeta serviceMeta);

    /**
     * Publish multiple service.
     *
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
     * Stop a service.
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
     * Shutdown the {@link ServiceProvider}.
     */
    void shutdown();
}