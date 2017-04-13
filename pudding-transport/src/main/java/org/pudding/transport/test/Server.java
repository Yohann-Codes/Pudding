package org.pudding.transport.test;

import org.pudding.transport.api.Acceptor;
import org.pudding.transport.netty.NettyTcpAcceptor;

/**
 * @author Yohann.
 */
public class Server {
    public static void main(String[] args) {
        Acceptor acceptor = new NettyTcpAcceptor();
        acceptor.withProcessor(new MyProcessor());
        try {
            acceptor.bind(20001);
            acceptor.bind(20002);
            acceptor.bind(20003);
        } catch (InterruptedException e) {
            acceptor.shutdownGracefully();
        }
    }
}
