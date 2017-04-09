package org.pudding.transport.test;

import org.pudding.transport.api.Acceptor;
import org.pudding.transport.netty.NettyTcpAcceptor;

/**
 * @author Yohann.
 */
public class Server {
    public static void main(String[] args) {
        Acceptor acceptor = new NettyTcpAcceptor();
        acceptor.processor(new MyProcessor());
        try {
            acceptor.bind(20001);
        } catch (InterruptedException e) {
            acceptor.shutdownGracefully();
        }
    }
}
