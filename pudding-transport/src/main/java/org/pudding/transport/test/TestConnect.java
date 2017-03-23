package org.pudding.transport.test;

import org.pudding.transport.netty.INettyConnector;
import org.pudding.transport.netty.NettyConnector;

/**
 * @author Yohann.
 */
public class TestConnect {
    public static void main(String[] args) {
        // connect
        INettyConnector connector = new NettyConnector();
        try {
            connector.connect("127.0.0.1", 20000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connector.shutdownGracefully();
        }
    }
}
