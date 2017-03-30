package org.pudding.rpc.consumer.service;

import org.pudding.common.model.ServiceMeta;

/**
 * 管理订阅到本地的服务信息.
 *
 * @author Yohann.
 */
public interface ServiceManager {

    /**
     * 将订阅结果缓存到本地.
     *
     * @param serviceMeta
     */
    void cacheService(ServiceMeta serviceMeta);
}
