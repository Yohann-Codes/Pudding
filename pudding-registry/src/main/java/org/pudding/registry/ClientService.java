package org.pudding.registry;

import org.pudding.transport.api.Channel;

import java.net.SocketAddress;

/**
 * Client service (interact with provider/consumer servers).
 *
 * @author Yohann.
 */
public interface ClientService {

    /**
     * Start registry server to listen.
     *
     * @param localAddress
     */
    Channel startRegistry(SocketAddress localAddress);

    /**
     * Close registry server.
     */
    void closeRegistry();

    /**
     * Shutdown resource.
     */
    void shutdown();
}
