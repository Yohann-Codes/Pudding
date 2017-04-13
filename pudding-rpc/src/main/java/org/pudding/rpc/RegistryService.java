package org.pudding.rpc;

import org.pudding.common.model.ServiceMeta;
import org.pudding.transport.api.Processor;

import java.net.SocketAddress;

/**
 * Registry service.
 * <p>
 * 1). Connect to registry server.
 * 2). Register service.
 * 3). Unregister service.
 * 4). Subscribe service.
 *
 * @author Yohann.
 */
public interface RegistryService {

    /**
     * Connect to registry server.
     *
     * @param address
     */
    void connectRegistry(SocketAddress... address);

    /**
     * Disconnect to registry.
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
     * Shutdown task queue.
     */
    void shutdown();

    /**
     * Return true if the task queue has shutdown, or false.
     */
    boolean isShutdown();
}
