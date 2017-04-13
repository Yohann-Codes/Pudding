package org.pudding.rpc;

import org.pudding.common.model.ServiceMeta;

/**
 * ServiceMeta factory.
 *
 * @author Yohann.
 */
public interface ServiceMetaFactory {

    /**
     * Create a {@link ServiceMeta} for publishing service.
     *
     * @param service
     * @param serviceAddress
     * @param weight
     * @return ServiceMeta.
     */
    ServiceMeta newPublishMeta(Object service, String serviceAddress, int weight);

    /**
     * Create a {@link ServiceMeta} for subscribing service.
     *
     * @param serviceName
     * @return
     */
    ServiceMeta newSubscribeMeta(String serviceName);
}
