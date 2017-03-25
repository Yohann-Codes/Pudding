package org.pudding.transport.test;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.ConnectNettyConfig;
import org.pudding.transport.netty.NettyConnector;

/**
 * @author Yohann.
 */
public class ConnectTest {
    public static void main(String[] args) {
        NettyConnector connector = new NettyConnector();
        Future future = connector.connect("127.0.0.1", 20000);
        Channel channel = future.channel();
        System.out.println(channel);
    }
}
