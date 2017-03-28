package org.pudding.rpc.provider.processor;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;

import static org.pudding.common.protocol.ProtocolHeader.dataPacketCode;

/**
 * Provider Processor implementation.
 *
 * @author Yohann.
 */
public class ProviderProcessor implements Processor {
    private static final Logger logger = Logger.getLogger(ProviderProcessor.class);

    public static final ProviderProcessor PROCESSOR = new ProviderProcessor();

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
                    case ProtocolHeader.PUBLISH_SERVICE:
                        ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);
                        publishServiceResponse(resultCode, serviceMeta);
                        break;
                }
                break;
        }
    }

    private void publishServiceResponse(int resultCode, ServiceMeta serviceMeta) {
        if (resultCode == ProtocolHeader.PUBLISH_SUCCESS) {
            logger.info("服务发布成功: " + serviceMeta);
        } else if (resultCode == ProtocolHeader.PUBLISH_FAILED_PUBLISHED) {
            logger.info("服务发布失败，该服务已发布：" + serviceMeta);
        }
    }

    @Override
    public void channelActive(Channel channel) {
    }

    @Override
    public void channelInactive(Channel channel) {
    }
}
