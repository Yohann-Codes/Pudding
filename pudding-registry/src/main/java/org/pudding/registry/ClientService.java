package org.pudding.registry;

import org.pudding.common.model.DispatchMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.transport.api.Channel;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Client service (interact with provider/consumer servers).
 *
 * @author Yohann.
 */
public interface ClientService {

    /**
     * Start registry_cluster server to listen.
     *
     * @param localAddress
     */
    Channel startRegistry(SocketAddress localAddress);

    /**
     * Close registry_cluster server.
     */
    void closeRegistry();

    /**
     * Register service.
     *
     * @param serviceMeta
     */
    void registerService(ServiceMeta serviceMeta);

    /**
     * Unregister service.
     *
     * @param serviceMeta
     */
    void unregisterService(ServiceMeta serviceMeta);

    /**
     * Subscribe service.
     *
     * @param serviceMeta
     */
    boolean subscribeService(ServiceMeta serviceMeta, Channel channel);

    /**
     * Bind the {@link ClusterService}.
     *
     * @param service
     */
    void withClusterService(ClusterService service);

    /**
     * Shutdown resource.
     */
    void shutdown();
}
