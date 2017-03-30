package org.pudding.registry.service;

import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.exception.ServiceSubscribeFailedException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.SubscribeResult;

/**
 * 管理和维护已发布的服务.
 * 存在并发性.
 *
 * @author Yohann.
 */
public interface ServiceManager {

    /**
     * 注册Provider发布的服务.
     *
     * @param serviceMeta
     */
    ServiceMeta registerService(ServiceMeta serviceMeta) throws ServicePublishFailedException;

    /**
     *
     *
     * @param serviceMeta
     */
    SubscribeResult subscribeService(ServiceMeta serviceMeta);
}
