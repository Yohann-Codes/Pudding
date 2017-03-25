package org.pudding.example;

import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.serialization.api.SerializerType;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.common.MessageHolder;
import org.pudding.transport.common.ProtocolHeader;

/**
 * @author Yohann.
 */
public class ServerProcessor implements Processor {
    @Override
    public void handleMessage(Channel channel, MessageHolder holder) {
        ProtocolHeader header = holder.getHeader();
        byte[] body = holder.getBody();

        System.out.println(header.getMagic());
        System.out.println(header.getType());
        System.out.println(header.getSign());
        System.out.println(header.getId());
        System.out.println(header.getErrorCode());
        System.out.println(header.getBodyLength());

        Serializer serializer = SerializerFactory.getSerializer(SerializerType.GSON.value());
        Service service = serializer.readObject(body, Service.class);
        System.out.println(service.getName() + " " + service.getVersion());
    }
}
