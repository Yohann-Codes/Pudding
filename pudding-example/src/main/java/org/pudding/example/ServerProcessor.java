package org.pudding.example;

import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.serialization.api.SerializerType;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.common.MessageHolder;
import org.pudding.common.ProtocolHeader;

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
