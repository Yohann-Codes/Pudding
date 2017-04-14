package org.pudding.transport.api;

import org.pudding.transport.netty.NettyTcpConnector;

import java.net.SocketAddress;

/**
 * The client side.
 *
 * @author Yohann.
 */
public interface Connector {

    /**
     * Connects the {@link Channel}'s socket to a remote address.
     *
     * @param host
     * @param port
     */
    Channel connect(String host, int port) throws InterruptedException;

    /**
     * Connects the {@link Channel}'s socket to a remote address.
     *
     * @param remoteAddress
     * @return if failed, return null.
     */
    Channel connect(SocketAddress remoteAddress) throws InterruptedException;

    /**
     * Connects the {@link Channel}'s socket to a remote address.
     * <p>
     * Reconnection strategy:
     * Select one address to reconnect.
     *
     * @param pattern
     * @param remoteAddress
     * @return if failed, return null.
     */
    Channel connect( NettyTcpConnector.ReconnPattern pattern, SocketAddress... remoteAddress) throws InterruptedException;

    /**
     * Binds the rpc processor.
     */
    void withProcessor(Processor processor);

    /**
     * Shutdown the client gracefully;
     */
    void shutdownGracefully();
}
