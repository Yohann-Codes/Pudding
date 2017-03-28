package org.pudding.rpc.provider;

import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.exception.ServiceStartFailedException;
import org.pudding.common.model.ServiceMeta;

/**
 * 服务提供者.
 * <p>
 * 1) 连接注册中心.
 * 2) 发布服务.
 * 3) 启动服务.
 * 4) 取消已发布服务.
 *
 * @author Yohann.
 */
public interface ServiceProvider {

    /**
     * 连接注册中心(目前注册中心只支持单机模式)
     * 注意: 调用此方法请必须在ProviderConfig中配置过registryAddress, 否则抛异常NullPointerException.
     */
    ServiceProvider connectRegistry();

    /**
     * 连接注册中心(目前注册中心只支持单机模式).
     *
     * @param registryAddress 格式: "host:port"  例如: "127.0.0.1:20000"
     */
    ServiceProvider connectRegistry(String registryAddress);

    /**
     * 关闭与注册中心的连接.
     * 如果发布完服务后较长一段时间不需要再发布，就可以调用此方法断开连接，
     * 下次发布前调用connectRegistry()建立连接即可.
     */
    void closeRegistry();

    /**
     * 发布一个服务.
     *
     * @param serviceMeta
     */
    ServiceProvider publishService(ServiceMeta serviceMeta) throws ServicePublishFailedException;

    /**
     * 发布多个服务.
     *
     * @param serviceMetas
     */
    ServiceProvider publishServices(ServiceMeta... serviceMetas) throws ServicePublishFailedException;

    /**
     * 启用本地服务(绑定端口监听远程调用).
     * 注意: 调用此方法之前必须先发布服务.
     */
    ServiceProvider startService() throws ServiceStartFailedException;

    /**
     * 发布并启用一个服务.
     *
     * @param serviceMeta
     */
    ServiceProvider publishAndStartService(ServiceMeta serviceMeta)
            throws ServicePublishFailedException, ServiceStartFailedException;

    /**
     * 发布并启用多个服务.
     *
     * @param serviceMetas
     */
    ServiceProvider publishAndStartServices(ServiceMeta... serviceMetas)
            throws ServiceStartFailedException, ServicePublishFailedException;

    /**
     * 取消已发布的服务.
     *
     * @param serviceMeta
     */
    ServiceProvider unpublishService(final ServiceMeta serviceMeta);

    /**
     * 停止服务.
     *
     * @param serviceMeta
     */
    ServiceProvider stopService(ServiceMeta serviceMeta);

        /**
         * 取消发布并停止服务.
         *
         * @param serviceMeta
         */
    ServiceProvider unpublishAndStopService(ServiceMeta serviceMeta);

    /**
     * 取消发布并停止在此实例上的全部服务.
     */
    ServiceProvider unpublishAndStopAll();
}