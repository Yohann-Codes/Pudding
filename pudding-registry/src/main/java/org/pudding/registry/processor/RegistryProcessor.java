package org.pudding.registry.processor;

import org.apache.log4j.Logger;
import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.registry.service.DefaultServiceManager;
import org.pudding.registry.service.ServiceManager;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;

/**
 * Registry Processor implementation.
 *
 * @author Yohann.
 */
public class RegistryProcessor extends RegistryExecutor implements Processor {
    private static final Logger logger = Logger.getLogger(RegistryProcessor.class);

    public static final RegistryProcessor PROCESSOR = new RegistryProcessor();

    private ServiceManager serviceManager; // 服务管理

    public RegistryProcessor() {
        super(RegistryConfig.nWorkers());
        serviceManager = new DefaultServiceManager();
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
                byte packetType = ProtocolHeader.dataPacketCode(header.getType());
                byte sign = header.getSign();
                int resultCode = header.getResultCode();
                byte[] body = holder.getBody();
                dispatch(channel, serializationType, packetType, sign, resultCode, body);
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
     * @param resultCode
     * @param body
     */
    private void dispatch(Channel channel, byte serializationType,
                          byte packetType, byte sign, int resultCode, byte[] body) {
        Serializer serializer = SerializerFactory.getSerializer(serializationType);

        switch (packetType) {
            case ProtocolHeader.REQUEST:
                switch (sign) {
                    case ProtocolHeader.PUBLISH_SERVICE:
                        ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);
                        registerService(channel, serviceMeta);
                        break;
                }
                break;

            case ProtocolHeader.RESPONSE:
                break;
        }
    }

    private void registerService(Channel channel, ServiceMeta serviceMeta) {
        MessageHolder holder;
        Serializer serializer = SerializerFactory.getSerializer(RegistryConfig.serializerType());
        byte[] body = serializer.writeObject(serviceMeta);

        try {
            serviceManager.registerService(serviceMeta);
            holder = MessageHolderFactory.newPublishServiceResponseHolder(body,
                    RegistryConfig.serializerType(), ProtocolHeader.PUBLISH_SUCCESS);
        } catch (ServicePublishFailedException e) {
            logger.info("注册失败: The service has been registered: " + serviceMeta);
            holder = MessageHolderFactory.newPublishServiceResponseHolder(body,
                    RegistryConfig.serializerType(), ProtocolHeader.PUBLISH_FAILED_PUBLISHED);
        }
        channel.write(holder);
    }

    @Override
    public void channelActive(Channel channel) {
    }

    @Override
    public void channelInactive(Channel channel) {
    }
}
