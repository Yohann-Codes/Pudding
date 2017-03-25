package org.pudding.transport.test;

import org.pudding.transport.netty.NettyAcceptor;

/**
 * @author Yohann.
 */
public class Server {
    public static void main(String[] args) {
        NettyAcceptor acceptor = new NettyAcceptor(new ServerProcessor());
        acceptor.bind(20000);
    }
}
