package org.pudding.transport.test;

import org.pudding.transport.netty.NettyAcceptor;

/**
 * @author Yohann.
 */
public class BindTest {
    public static void main(String[] args) {
        NettyAcceptor acceptor = new NettyAcceptor();
        acceptor.bind(20000);
    }
}
