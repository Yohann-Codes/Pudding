package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;

/**
 * Weighted random load balancer.
 *
 * @author Yohann.
 */
public class WgtRandomLoadBalancer implements LoadBalancer {
    @Override
    public ServiceMeta select(String serviceName) {
        return null;
    }
}
