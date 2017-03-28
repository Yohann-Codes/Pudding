package org.pudding.registry.service;

import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.model.ServiceMeta;

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
     * 取消已注册的服务.
     *
     * @param serviceMeta
     */
    ServiceMeta unregisterService(ServiceMeta serviceMeta);
}
