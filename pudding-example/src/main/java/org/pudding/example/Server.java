package org.pudding.example;

import org.pudding.transport.api.Acceptor;
import org.pudding.transport.netty.NettyAcceptor;

/**
 * @author Yohann.
 */
public class Server {
    public static void main(String[] args) {
        Acceptor acceptor = new NettyAcceptor(new ServerProcessor());
        acceptor.bind(20000);
    }
}
