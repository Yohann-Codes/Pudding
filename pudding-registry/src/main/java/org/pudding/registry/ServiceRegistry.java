package org.pudding.registry;

/**
 * 控制注册中心服务器.
 *
 * @author Yohann.
 */
public interface ServiceRegistry {

    /**
     * 启动注册中心, 默认端口20000.
     * 调用此方法前，可以在RegistryConfig中配置端口号.
     */
    void startRegistry();

    /**
     * 启动注册中心.
     *
     * @param port
     */
    void startRegistry(int port);

    /**
     * 关闭注册中心.
     */
    void closeRegistry();
}
