package org.pudding.transport.abstraction;

import org.pudding.transport.SocketOption;

import java.net.SocketAddress;

/**
 * 抽象出来的TCP连接者.
 *
 * @author Yohann.
 */
public interface Connector {

    /**
     * 发起连接.
     *
     * @param remote
     * @return
     */
    PudChannelFuture connect(SocketAddress remote);

    /**
     * 配置Socket选项，支持链式调用.
     *
     * @param option
     */
    <T> Acceptor socketOptions(SocketOption<T> option, Object value);
}
