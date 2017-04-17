package org.pudding.registry;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.SubscriberMeta;

import java.net.SocketAddress;

/**
 * Cluster service (interact with other registry_cluster servers).
 *
 * @author Yohann.
 */
public interface ClusterService {

    /**
     * Connect with registry_cluster cluster.
     *
     * @param prevAddress
     */
    void connectCluster(SocketAddress... prevAddress);

    /**
     * Disconnect with registry_cluster cluster.
     */
    void disconnectCluster();

    /**
     * Sync the service that has published.
     *
     * @param originId
     * @param serviceMeta
     */
    void servicePublishSync(long originId, ServiceMeta serviceMeta);

    /**
     * Sync the service that has unpublished.
     *
     * @param originId
     * @param serviceMeta
     */
    void serviceUnpublishSync(long originId, ServiceMeta serviceMeta);

    /**
     * Sync the subscriber.
     *
     * @param originId
     * @param subscriberMeta
     */
    void serviceSubscribeSync(long originId, SubscriberMeta subscriberMeta);

    /**
     * Bind the {@link ClientService}.
     *
     * @param service
     */
    void withClientService(ClientService service);

    /**
     * Shutdown resource.
     */
    void shutdown();
}
