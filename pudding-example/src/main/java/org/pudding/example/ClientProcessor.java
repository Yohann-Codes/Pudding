package org.pudding.example;

import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.common.protocol.MessageHolder;

/**
 * @author Yohann.
 */
public class ClientProcessor implements Processor {

    @Override
    public void channelRead(Channel channel, MessageHolder holder) {

    }

    @Override
    public void channelActive(Channel channel) {
        System.out.println("Client Active");
    }

    @Override
    public void channelInactive(Channel channel) {
        System.out.println("Client Inactive");
    }
}
