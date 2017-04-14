package org.pudding.rpc;

import org.pudding.common.model.ServiceMeta;
import org.pudding.transport.api.Processor;

import java.net.SocketAddress;

/**
 * Registry service (interact with registry_cluster server).
 * <p>
 * 1). Connect to registry_cluster server.
 * 2). Register service.
 * 3). Unregister service.
 * 4). Subscribe service.
 *
 * @author Yohann.
 */
public interface RegistryService {

    /**
     * Connect to registry_cluster server.
     *
     * @param address
     */
    void connectRegistry(SocketAddress... address);

    /**
     * Disconnect to registry_cluster.
     */
    void disconnectRegistry();

    /**
     * Register service that will be published.
     *
     * @param serviceMeta
     */
    void register(ServiceMeta serviceMeta);

    /**
     * Unregister service that will be unpublished.
     *
     * @param serviceMeta
     */
    void unregister(ServiceMeta serviceMeta);

    /**
     * Subscribe service.
     *
     * @param serviceMeta
     */
    void subscribe(ServiceMeta serviceMeta);

    /**
     * Shutdown resource.
     */
    void shutdown();
}
