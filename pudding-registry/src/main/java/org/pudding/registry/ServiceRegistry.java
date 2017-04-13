package org.pudding.registry.config;

import org.pudding.transport.api.Channel;

import java.net.SocketAddress;

/**
 * Service registry.
 *
 * @author Yohann.
 */
public interface ServiceRegistry {

    /**
     * Start registry.
     *
     * @param port
     */
    Channel startRegistry(int port);

    /**
     * Start registry.
     *
     * @param address
     */
    Channel startRegistry(SocketAddress address);

    /**
     * Close registry.
     */
    void closeRegistry();
}
