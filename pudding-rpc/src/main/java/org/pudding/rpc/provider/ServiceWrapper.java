package org.pudding.rpc.provider;

import org.pudding.common.model.ServiceMeta;

/**
 * 将需要发布的服务包装成ServiceMeta.
 *
 * @author Yohann.
 */
public interface ServiceWrapper {

    /**
     * 构建服务元数据.
     *
     * @param service 服务对象
     * @param serviceAddress 服务地址 [host:port]
     * @return ServiceMeta.
     */
    ServiceMeta build(Object service, String serviceAddress);
}
