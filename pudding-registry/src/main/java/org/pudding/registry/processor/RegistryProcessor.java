package org.pudding.registry.processor;

import org.apache.log4j.Logger;
import org.pudding.common.exception.ServicePublishFailedException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.Services;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.registry.DefaultServiceRegistry;
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
public class  RegistryProcessor extends RegistryExecutor implements Processor {
    private static final Logger logger = Logger.getLogger(RegistryProcessor.class);

    private ServiceManager serviceManager; // 服务管理

    private DefaultServiceRegistry serviceRegistry;

    public RegistryProcessor(DefaultServiceRegistry defaultServiceRegistry, int nWorkers) {
        super(nWorkers);
        serviceRegistry = defaultServiceRegistry;
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
        ServiceMeta serviceMeta;

        switch (packetType) {
            case ProtocolHeader.REQUEST:
                switch (sign) {
                    case ProtocolHeader.PUBLISH_SERVICE:
                        serviceMeta = serializer.readObject(body, ServiceMeta.class);
                        registerService(channel, serviceMeta);
                        break;
                    case ProtocolHeader.SUBSCRIBE_SERVICE:
                        serviceMeta = serializer.readObject(body, ServiceMeta.class);
                        subscribeService(channel, serviceMeta);
                        break;
                }
                break;

            case ProtocolHeader.RESPONSE:
                break;
        }
    }

    /**
     * 执行注册.
     *
     * @param channel
     * @param serviceMeta
     */
    private void registerService(Channel channel, ServiceMeta serviceMeta) {
        MessageHolder holder;
        Serializer serializer = SerializerFactory.getSerializer(RegistryConfig.serializerType());
        byte[] body = serializer.writeObject(serviceMeta);

        try {
            serviceManager.registerService(serviceMeta);
            holder = MessageHolderFactory.newPublishServiceResponseHolder(body,
                    RegistryConfig.serializerType(), ProtocolHeader.SUCCESS);
            logger.info("Register Success: " + serviceMeta);
        } catch (ServicePublishFailedException e) {
            logger.info("Register Failed: " + serviceMeta);
            holder = MessageHolderFactory.newPublishServiceResponseHolder(body,
                    RegistryConfig.serializerType(), ProtocolHeader.FAILED);
        }
        channel.write(holder);
    }

    /**
     * 执行订阅.
     *
     * @param channel
     * @param serviceMeta
     */
    private void subscribeService(Channel channel, ServiceMeta serviceMeta) {
        MessageHolder holder;
        Services services = serviceManager.subscribeService(serviceMeta);
        Serializer serializer = SerializerFactory.getSerializer(RegistryConfig.serializerType());
        byte[] body = serializer.writeObject(services);

        if (services.getServiceMetas() != null) {
            // 订阅成功
            holder = MessageHolderFactory.newSubscribeServiceResponseHolder(body,
                    RegistryConfig.serializerType(), ProtocolHeader.SUCCESS);
            logger.info("Allot Success: " + services);
        } else {
            // 订阅失败
            holder = MessageHolderFactory.newSubscribeServiceResponseHolder(body,
                    RegistryConfig.serializerType(), ProtocolHeader.FAILED);
            logger.info("Allot Failed: " + services);
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
