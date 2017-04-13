package org.pudding.rpc.provider;

import org.pudding.common.model.ServiceMeta;
import org.pudding.transport.api.Channel;

/**
 * Client service (interact with consumer servers).
 *
 * @author Yohann.
 */
public interface ClientService {

    /**
     * Start the specified service to listen.
     *
     * @param serviceMeta
     */
    Channel startService(ServiceMeta serviceMeta);

    /**
     * Stop the specified service.
     */
    void stopService(ServiceMeta serviceMeta, Channel channel);

    /**
     * shutdown.
     */
    void shutdown();
}
