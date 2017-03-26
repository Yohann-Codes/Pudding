package org.pudding.example;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.common.protocol.MessageHolder;

/**
 * @author Yohann.
 */
public class ServerProcessor implements Processor {

    @Override
    public void channelRead(Channel channel, MessageHolder holder) {
        System.out.println("ServerProcessor channelRead");
    }

    @Override
    public void channelActive(Channel channel) {
        System.out.println("Server Active");
    }

    @Override
    public void channelInactive(Channel channel) {
        System.out.println("Server Inactive");
    }
}
