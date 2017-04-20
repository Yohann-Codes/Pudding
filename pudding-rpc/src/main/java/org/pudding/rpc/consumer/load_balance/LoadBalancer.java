package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;

/**
 * load balancer.
 * Select a {@link ServiceMeta}.
 *
 * @author Yohann.
 */
public interface LoadBalancer {

    /**
     * Select a channel of specific service.
     */
    ServiceMeta select(String serviceName);
}
