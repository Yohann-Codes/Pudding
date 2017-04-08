package org.pudding.transport.test;

import org.pudding.transport.netty.NettyTcpConnector;

/**
 * @author Yohann.
 */
public class Client_2 {
    public static void main(String[] args) {
        NettyTcpConnector connector = new NettyTcpConnector();
        connector.processor(new MyProcessor());
        try {
            connector.connect("127.0.0.1", 20001);
        } catch (InterruptedException e) {
            connector.shutdownGracefully();
        }
    }
}
