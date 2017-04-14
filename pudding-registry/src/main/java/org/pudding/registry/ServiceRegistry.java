package org.pudding.registry;

import org.pudding.transport.api.Channel;

import java.net.SocketAddress;

/**
 * Service registry_cluster.
 *
 * @author Yohann.
 */
public interface ServiceRegistry {

    /**
     * Start registry_cluster.
     *
     * @param port
     */
    Channel startRegistry(int port);

    /**
     * Start registry_cluster.
     *
     * @param address
     */
    Channel startRegistry(SocketAddress address);

    /**
     * Close registry_cluster.
     */
    void closeRegistry();
}
