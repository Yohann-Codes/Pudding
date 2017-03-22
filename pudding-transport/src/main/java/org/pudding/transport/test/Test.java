package org.pudding.transport.test;

import org.pudding.transport.netty.NettyAcceptor;

/**
 * @author Yohann.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        // bind
        NettyAcceptor acceptor = new NettyAcceptor();
        acceptor.bind(20000);
    }
}
