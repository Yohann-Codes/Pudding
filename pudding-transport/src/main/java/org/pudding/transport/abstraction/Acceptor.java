package org.pudding.transport.abstraction;

import org.pudding.transport.SevSocketOption;
import org.pudding.transport.SocketOption;
import org.pudding.transport.netty.NettyOption;

import java.net.SocketAddress;

/**
 * 抽象出来的TCP接受者.
 *
 * @author Yohann.
 */
public interface Acceptor {

    /**
     * 绑定本地，启动监听.
     *
     * @param port
     * @return
     */
    PudChannelFuture bind(int port);

    /**
     * 绑定本地，启动监听.
     *
     * @param host
     * @param port
     * @return
     */
    PudChannelFuture bind(String host, int port);

    /**
     * 绑定本地，启动监听.
     *
     * @param local
     * @return
     */
    PudChannelFuture bind(SocketAddress local);

    /**
     * 配置ServerSocket选项，支持链式调用.
     *
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    <T> Acceptor sevSocketOptions(SevSocketOption<T> option, T value);

    /**
     * 获取ServerSocket选项值.
     *
     * @param option
     * @param <T>
     * @return
     */
    <T> T sevSocketOption(SevSocketOption<T> option);

    /**
     * 配置Socket选项，支持链式调用.
     *
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    <T> Acceptor socketOptions(SocketOption<T> option, T value);

    /**
     * 获取Socket选项值.
     *
     * @param option
     * @param <T>
     * @return
     */
    <T> T socketOption(SocketOption<T> option);

    /**
     *
     * 其它配置，如框架本身的配置，支持链式调用.
     *
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    <T> Acceptor otherOptions(NettyOption<T> option, T value);

    /**
     * 获取其它选项值，如框架本身.
     *
     * @param option
     * @param <T>
     * @return
     */
    <T> T otherOption(NettyOption<T> option);
}
