package org.pudding.rpc.provider.processor;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.config.ProviderConfig;
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
public class ProviderProcessor extends ProviderExecutor implements Processor {
    private static final Logger logger = Logger.getLogger(ProviderProcessor.class);

    private DefaultServiceProvider serviceProvider;

    public ProviderProcessor(DefaultServiceProvider serviceProvider, int nWorkers) {
        super(nWorkers);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void channelRead(final Channel channel, final MessageHolder holder) {

        // 由线程池执行
        execute(new Runnable() {
            @Override
            public void run() {
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
        });
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
                        serviceProvider.processPublishResult(serviceMeta, resultCode);
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
