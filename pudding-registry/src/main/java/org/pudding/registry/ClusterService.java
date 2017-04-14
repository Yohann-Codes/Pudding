package org.pudding.registry;

import java.net.SocketAddress;

/**
 * Cluster service (interact with other registry servers).
 *
 * @author Yohann.
 */
public interface ClusterService {

    /**
     * Connect with registry cluster.
     *
     * @param prevAddress
     */
    void connectCluster(SocketAddress... prevAddress);

    /**
     * Disconnect with registry cluster.
     */
    void disconnectCluster();

    /**
     * Shutdown resource.
     */
    void shutdown();
}
