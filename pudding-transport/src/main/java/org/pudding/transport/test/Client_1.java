package org.pudding.transport.test;

import org.pudding.transport.api.Connector;
import org.pudding.transport.netty.NettyTcpConnector;

import java.net.InetSocketAddress;

/**
 * @author Yohann.
 */
public class Client_1 {
    public static void main(String[] args) {
        Connector connector = new NettyTcpConnector();
        connector.withProcessor(new MyProcessor());
        try {
            InetSocketAddress address1 = new InetSocketAddress("127.0.0.1", 20001);
            InetSocketAddress address2 = new InetSocketAddress("127.0.0.1", 20002);
            InetSocketAddress address3 = new InetSocketAddress("127.0.0.1", 20003);
            connector.connect(address1, address2, address3);
        } catch (InterruptedException e) {
            connector.shutdownGracefully();
        }
    }
}
