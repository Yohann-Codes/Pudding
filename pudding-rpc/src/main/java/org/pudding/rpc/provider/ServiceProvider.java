package org.pudding.rpc.provider;

import org.pudding.common.exception.NotConnectRegistryException;
import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.exception.ServiceNotPublishedException;
import org.pudding.rpc.model.Service;

/**
 * 服务提供者.
 *
 * 最终要的两个功能:
 *      1) 连接注册中心发布服务.
 *      2) 启动本地服务.
 *
 * @author Yohann.
 */
public interface ServiceProvider {

    /**
     * 连接注册中心(目前注册中心只支持单机模式).
     *
     * @param registryAddress 格式: "host:port"  例如: "127.0.0.1:20000"
     */
    ServiceProvider connectRegistry(String registryAddress) throws RepeatConnectRegistryException;

    /**
     * 发布一个服务.
     *
     * @param service
     */
    ServiceProvider publishService(Service service) throws NotConnectRegistryException;

    /**
     * 发布多个服务.
     *
     * @param services
     */
    ServiceProvider publishServices(Service... services) throws NotConnectRegistryException;

    /**
     * 启动本地服务(绑定端口监听远程调用).
     * 注意: 调用此方法之前必须先发布服务.
     */
    ServiceProvider startService() throws ServiceNotPublishedException;

    /**
     * 发布并启动一个服务.
     *
     * @param service
     */
    ServiceProvider publishAndStartService(Service service) throws NotConnectRegistryException;

    /**
     * 发布并启动多个服务.
     *
     * @param services
     */
    ServiceProvider publishAndStartServices(Service... services) throws NotConnectRegistryException;
}
