package org.pudding.rpc.consumer.processor;

import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.SubscribeResult;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;

import static org.pudding.common.protocol.ProtocolHeader.dataPacketCode;

/**
 * Consumer Processor implementation.
 *
 * @author Yohann.
 */
public class ConsumerProcessor implements Processor {

    private DefaultServiceConsumer serviceConsumer;

    public ConsumerProcessor(DefaultServiceConsumer serviceConsumer) {
        this.serviceConsumer = serviceConsumer;
    }

    @Override
    public void channelRead(Channel channel, MessageHolder holder) {
        // 解析MessageHolder
        ProtocolHeader header = holder.getHeader();
        byte serializationType = ProtocolHeader.serializationCode(header.getType());
        byte packetType = dataPacketCode(header.getType());
        byte sign = header.getSign();
        long invokeId = header.getInvokeId();
        int resultCode = header.getResultCode();
        byte[] body = holder.getBody();
        dispatch(channel, serializationType, packetType, sign, invokeId, resultCode, body);
    }

    /**
     * 消息分派.
     *
     * @param channel
     * @param serializationType
     * @param packetType
     * @param sign
     * @param invokeId
     * @param resultCode
     * @param body
     */
    private void dispatch(Channel channel, byte serializationType,
                          byte packetType, byte sign, long invokeId, int resultCode, byte[] body) {
        Serializer serializer = SerializerFactory.getSerializer(serializationType);

        switch (packetType) {
            case ProtocolHeader.REQUEST:
                break;

            case ProtocolHeader.RESPONSE:
                switch (sign) {
                    case ProtocolHeader.SUBSCRIBE_SERVICE:
                        SubscribeResult subscribeResult = serializer.readObject(body, SubscribeResult.class);
                        serviceConsumer.processSubscribeResult(subscribeResult, resultCode);
                        break;
                }
                break;
        }
    }

    @Override
    public void channelActive(Channel channel) {

    }

    @Override
    public void channelInactive(Channel channel) {

    }
}
