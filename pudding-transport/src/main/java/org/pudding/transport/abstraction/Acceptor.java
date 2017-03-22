package org.pudding.transport.abstraction;

import java.net.SocketAddress;

/**
 * TCP接受者.
 *
 * @author Yohann.
 */
public interface Acceptor {

    /**
     * 返回绑定地址.
     *
     * @return
     */
    SocketAddress localAddress();

    /**
     * 绑定本地，启动监听.
     *
     * @param port
     */
    void bind(int port) throws Exception;

    /**
     * 绑定本地，启动监听.
     *
     * @param host
     * @param port
     */
    void bind(String host, int port) throws Exception;

    /**
     * 绑定本地，启动监听.
     *
     * @param local
     */
    void bind(SocketAddress local) throws Exception;

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
