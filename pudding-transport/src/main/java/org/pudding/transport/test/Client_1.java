package org.pudding.transport.test;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.netty.NettyTcpConnector;

/**
 * @author Yohann.
 */
public class Client_1 {
    public static void main(String[] args) {
        Connector connector = new NettyTcpConnector();
        connector.withProcessor(new MyProcessor());
        try {
            connector.connect("127.0.0.1", 20001);
        } catch (InterruptedException e) {
            connector.shutdownGracefully();
        }
    }
}
