package org.pudding.transport.test;

import org.pudding.transport.netty.INettyAcceptor;
import org.pudding.transport.netty.NettyAcceptor;

/**
 * @author Yohann.
 */
public class Test {
    public static void main(String[] args) {
        // bind
        INettyAcceptor acceptor = new NettyAcceptor();
        try {
            acceptor.bind(20000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            acceptor.shutdownGracefully();
        }
    }
}
