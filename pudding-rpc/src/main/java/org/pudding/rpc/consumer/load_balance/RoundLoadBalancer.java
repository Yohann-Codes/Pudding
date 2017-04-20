package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.consumer.LocalServiceContainer;

import java.util.List;

/**
 * Round load balancer.
 *
 * @author Yohann.
 */
public class RoundLoadBalancer implements LoadBalancer {
    private final LocalServiceContainer localServiceContainer = new LocalServiceContainer();

    private int serviceIndex = 0;

    @Override
    public ServiceMeta select(String serviceName) {
        List<ServiceMeta> serviceMetas = localServiceContainer.get(serviceName);

        if (serviceMetas == null) {
            return null;
        }

        int size = serviceMetas.size();

        if (size == 1) {
            return serviceMetas.get(0);
        }

        synchronized (this) {
            if (serviceIndex > size - 1) {
                // reset
                serviceIndex = 0;
            } else {
                serviceIndex++;
            }
        }

        return serviceMetas.get(serviceIndex);
    }
}
