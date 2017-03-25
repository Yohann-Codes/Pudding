package org.pudding.transport.api;

import java.net.SocketAddress;

/**
 * 连接端.
 *
 * @author Yohann.
 */
public interface Connector {

    /**
     * 返回连接地址.
     *
     * @return
     */
    SocketAddress remoteAddress();

    /**
     * 连接对端.
     *
     * @param host
     * @param port
     */
    Future connect(String host, int port);

    /**
     * 连接对端.
     *
     * @param remoteAddress
     * @return if failed, return null.
     */
    Future connect(SocketAddress remoteAddress);

    /**
     * 返回配置对象Config.
     *
     * @return
     */
    Config config();

    /**
     * 关闭服务资源.
     *
     * @return
     */
    void shutdownGracefully();
}
