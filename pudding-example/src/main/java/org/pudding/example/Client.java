package org.pudding.example;

import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.serialization.api.SerializerType;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Future;
import org.pudding.transport.api.FutureListener;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.transport.netty.NettyConnector;

/**
 * @author Yohann.
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        // 连接目标主机
        Connector connector = new NettyConnector(new ClientProcessor());
        Future future = connector.connect("127.0.0.1", 20000);
        Channel channel = future.channel();

//        for (int i = 0; i < 100; i++) {
//            publish(channel);
//            Thread.sleep(500);
//        }
    }

    static void publish(Channel channel) {
        // 编织服务
        Service service = new Service();
        service.setName("Service");
        service.setVersion("1.0.0");

        // 序列化
        Serializer serializer = SerializerFactory.getSerializer(SerializerType.GSON.value());
        byte[] body = serializer.writeObject(service);

        // 构造消息
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header(body.length));
        holder.setBody(body);

        // 发送消息
        channel.write(holder).addListener(new FutureListener() {
            @Override
            public void operationComplete(boolean isSuccess) {
                System.out.println(isSuccess);
            }
        });
    }

    static ProtocolHeader header(int bodyLength) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(SerializerType.GSON.value(), ProtocolHeader.MESSAGE));
        header.setSign(ProtocolHeader.PUBLISH_SERVICE);
        header.setId(1);
        header.setErrorCode(0);
        header.setBodyLength(bodyLength);
        return header;
    }
}
