package org.pudding.rpc.consumer.load_balance;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.RandomUtil;
import org.pudding.rpc.consumer.LocalServiceContainer;

import java.util.List;

/**
 * Random load balancer.
 *
 * @author Yohann.
 */
public class RandomLoadBalancer implements LoadBalancer {
    private final LocalServiceContainer localServiceContainer = new LocalServiceContainer();

    @Override
    public ServiceMeta select(String serviceName) {
        ServiceMeta serviceMeta;
        List<ServiceMeta> serviceMetas = localServiceContainer.get(serviceName);

        if (serviceMetas == null) {
            return null;
        }

        int size = serviceMetas.size();
        if (size == 1) {
            serviceMeta = serviceMetas.get(0);
        } else {
            // [0, size)
            int index = RandomUtil.getInt(size);
            serviceMeta = serviceMetas.get(index);
        }
        return serviceMeta;
    }
}
