package org.pudding.registry;

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
     * Shutdown resource.
     */
    void shutdown();
}
