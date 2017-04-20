package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.DispatchMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.common.utils.SequenceUtil;
import org.pudding.rpc.consumer.DefaultServiceConsumer;
import org.pudding.rpc.consumer.LocalServiceContainer;
import org.pudding.rpc.consumer.router.Router;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelListener;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTcpConnector;
import org.pudding.transport.netty.NettyTransportFactory;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * The default implementation of {@link RegistryService}.
 *
 * @author Yohann.
 */
public class DefaultRegistryService extends AbstractRegistryService {
    private static final Logger logger = Logger.getLogger(DefaultRegistryService.class);

    // Process the registry_cluster task
    private final Processor registryProcessor = new RegistryProcessor();

    private final Connector connector = NettyTransportFactory.createTcpConnector();

    private final ConcurrentMap<Long, ServiceMeta> nonCompleteRegServices = Maps.newConcurrentHashMap();
    private final ConcurrentMap<Long, ServiceMeta> nonCompleteSubServices = Maps.newConcurrentHashMap();

    private final LocalServiceContainer localServiceContainer = new LocalServiceContainer();

    // Registry channel
    private volatile Channel channel;
    private volatile boolean isConnected = false;

    private final ExecutorService executor;

    private DefaultServiceProvider provider;
    private DefaultServiceConsumer consumer;

    public DefaultRegistryService(DefaultServiceProvider provider, ExecutorService executor) {
        connector.withProcessor(registryProcessor);
        this.provider = provider;
        this.executor = executor;
    }

    public DefaultRegistryService(DefaultServiceConsumer consumer, ExecutorService executor) {
        connector.withProcessor(registryProcessor);
        this.consumer = consumer;
        this.executor = executor;
    }

    @Override
    public void connectRegistry(SocketAddress... address) {
        checkNotShutdown();

        synchronized (this) {
            if (isConnected) {
                throw new IllegalStateException("has connected with registry, channel:" + channel);
            }
            try {
                // Connect to registry_cluster
                channel = connector.connect(NettyTcpConnector.ReconnPattern.CONNECT_RANDOM_ADDRESS, address);
                isConnected = true;

                logger.info("connect with registry, channel:" + channel);
            } catch (InterruptedException e) {
                logger.warn("connect with registry failed, channel:" + channel);
            }
        }
    }

    @Override
    public void disconnectRegistry() {
        synchronized (this) {
            if (!isConnected) {
                throw new IllegalStateException("not connect with registry");
            }

            if (channel != null) {
                channel.close();
            }
            logger.info("disconnect with registry, channel:" + channel);

            channel = null;
            isConnected = false;
        }
    }

    @Override
    protected void doRegister(final ServiceMeta serviceMeta) {
        Runnable registerTask = new Runnable() {

            @Override
            public void run() {
                byte serializationType = RpcConfig.getSerializationType();
                Serializer serializer = SerializerFactory.getSerializer(serializationType);
                byte[] body = serializer.writeObject(serviceMeta);

                final long sequence = SequenceUtil.generateSequence();

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type(serializationType, ProtocolHeader.REQUEST))
                        .setSign(ProtocolHeader.PUBLISH_SERVICE)
                        .setSequence(sequence)
                        .setStatus(0)
                        .setBodyLength(body.length);

                final Message message = new Message();
                message.setHeader(header)
                        .setBody(body);

                if (channel == null) {
                    throw new IllegalStateException("not connect with registry");
                }
                if (channel.isActive()) {
                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("publish-service-write success; serviceMeta:" + serviceMeta + "; channel:" + channel);

                            MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                            messagesNonAck.put(sequence, messageNonAck);
                            logger.info("put-ack:" + messageNonAck);

                            nonCompleteRegServices.put(sequence, serviceMeta);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("publish-service-write failed; serviceMeta:" + serviceMeta + "; channel:" + channel);
                        }
                    });
                } else {
                    logger.warn("publish-service-write failed, channel is not active; serviceMeta:" + serviceMeta + "; channel:" + channel);
                }
            }
        };

        executor.execute(registerTask);
    }

    @Override
    protected void doUnregister(final ServiceMeta serviceMeta) {
        Runnable registerTask = new Runnable() {

            @Override
            public void run() {
                byte serializationType = RpcConfig.getSerializationType();
                Serializer serializer = SerializerFactory.getSerializer(serializationType);
                byte[] body = serializer.writeObject(serviceMeta);

                final long sequence = SequenceUtil.generateSequence();

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type(serializationType, ProtocolHeader.REQUEST))
                        .setSign(ProtocolHeader.UNPUBLISH_SERVICE)
                        .setSequence(sequence)
                        .setStatus(0)
                        .setBodyLength(body.length);

                final Message message = new Message();
                message.setHeader(header)
                        .setBody(body);

                if (channel == null) {
                    throw new IllegalStateException("not connect with registry");
                }
                if (channel.isActive()) {
                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("unpublish-service-write success; serviceMeta:" + serviceMeta + "; channel:" + channel);

                            MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                            messagesNonAck.put(sequence, messageNonAck);
                            logger.info("put-ack:" + messageNonAck);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("unpublish-service-write failed; serviceMeta:" + serviceMeta + "; channel:" + channel);
                        }
                    });
                } else {
                    logger.warn("unpublish-service-write failed, channel is not active; serviceMeta:" + serviceMeta + "; channel:" + channel);
                }
            }
        };

        executor.execute(registerTask);
    }

    @Override
    protected void doSubscribe(final ServiceMeta serviceMeta) {
        Runnable registerTask = new Runnable() {

            @Override
            public void run() {
                byte serializationType = RpcConfig.getSerializationType();
                Serializer serializer = SerializerFactory.getSerializer(serializationType);
                byte[] body = serializer.writeObject(serviceMeta);

                final long sequence = SequenceUtil.generateSequence();

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type(serializationType, ProtocolHeader.REQUEST))
                        .setSign(ProtocolHeader.SUBSCRIBE_SERVICE)
                        .setSequence(sequence)
                        .setStatus(0)
                        .setBodyLength(body.length);

                final Message message = new Message();
                message.setHeader(header)
                        .setBody(body);

                if (channel == null) {
                    throw new IllegalStateException("not connect with registry");
                }
                if (channel.isActive()) {
                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("subscribe-service-write success; serviceMeta:" + serviceMeta + "; channel:" + channel);

                            MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                            messagesNonAck.put(sequence, messageNonAck);
                            logger.info("put-ack:" + messageNonAck);

                            nonCompleteSubServices.put(sequence, serviceMeta);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("subscribe-service-write failed; serviceMeta:" + serviceMeta + "; channel:" + channel);
                        }
                    });
                } else {
                    logger.warn("subscribe-service-write failed, channel is not active; serviceMeta:" + serviceMeta + "; channel:" + channel);
                }
            }
        };

        executor.execute(registerTask);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        connector.shutdownGracefully();
    }

    /**
     * The processor about registry.
     */
    private class RegistryProcessor implements Processor {

        @Override
        public void handleMessage(final Channel channel, final Message message) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    ProtocolHeader header = message.getHeader();
                    byte[] body = message.getBody();

                    // Parse header
                    byte serializationType = ProtocolHeader.serializationType(header.getType());
                    byte messageType = ProtocolHeader.messageType(header.getType());
                    long sequence = header.getSequence();
                    byte sign = header.getSign();
                    int status = header.getStatus();

                    switch (messageType) {
                        case ProtocolHeader.REQUEST:
                            // Reply ack
                            replyAcknowledge(channel, sequence);
                            switch (sign) {
                                case ProtocolHeader.DISPATCH_SERVICE:
                                    handleDispatchService(serializationType, body);
                                    break;
                                case ProtocolHeader.OFFLINE_SERVICE:
                                    handleServiceOffline(serializationType, body);
                                    break;
                            }
                            break;

                        case ProtocolHeader.RESPONSE:
                            switch (sign) {
                                case ProtocolHeader.PUBLISH_SERVICE:
                                    handlePublishResponse(sequence, status);
                                    break;
                                case ProtocolHeader.UNPUBLISH_SERVICE:
                                    handleUnpublishResponse(status);
                                    break;
                                case ProtocolHeader.SUBSCRIBE_SERVICE:
                                    handleSubscribeResponse(sequence, serializationType, body, status);
                                    break;
                            }
                            break;

                        case ProtocolHeader.ACK:
                            handleAcknowledge(sequence);
                            break;

                        default:
                            logger.warn("invalid message type: " + messageType);
                    }
                }
            });
        }

        /**
         * Reply ACK to peer.
         */
        private void replyAcknowledge(Channel channel, final long sequence) {
            ProtocolHeader header = new ProtocolHeader();
            header.setMagic(ProtocolHeader.MAGIC)
                    .setType(ProtocolHeader.type((byte) 0, ProtocolHeader.ACK))
                    .setSign((byte) 0)
                    .setSequence(sequence)
                    .setStatus(0)
                    .setBodyLength(0);

            Message message = new Message();
            message.setHeader(header)
                    .setBody(new byte[0]);

            if (channel.isActive()) {
                channel.write(message, new ChannelListener() {
                    @Override
                    public void operationSuccess(Channel channel) {
                        logger.info("ack-write success; sequence:" + sequence + "; channel:" + channel);
                    }

                    @Override
                    public void operationFailure(Channel channel, Throwable cause) {
                        logger.warn("ack-write failed; sequence:" + sequence + "; channel:" + channel, cause);
                    }
                });
            } else {
                logger.warn("ack-write failed, channel is not active; sequence:" + sequence + "; channel:" + channel);
            }
        }

        /**
         * Handle the service has dispatched.
         */
        private void handleDispatchService(byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);
            localServiceContainer.put(serviceMeta);
        }

        /**
         * Handle the service offline.
         */
        private void handleServiceOffline(byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);
            localServiceContainer.remove(serviceMeta);
        }

        /**
         * Handle the response message of publishing service.
         */
        private void handlePublishResponse(long sequence, int status) {
            if (status == ProtocolHeader.SUCCESS) {
                synchronized (nonCompleteRegServices) {
                    nonCompleteRegServices.remove(sequence);
                    if (nonCompleteRegServices.size() == 0) {
                        // all service have published successful
                        // notify the thread of publishing service
                        provider.timeout = false;
                        provider.lock.lock();
                        provider.notComplete.signalAll();
                        provider.lock.unlock();
                    }
                }
            }
        }

        /**
         * Handle the response message of unpublishing service.
         */
        private void handleUnpublishResponse(int status) {
            if (status == ProtocolHeader.SUCCESS) {
                logger.info("unpublish service successful");
            } else {
                logger.warn("unpublish service failed");
            }
        }

        /**
         * Handle the response message of subscribing service.
         */
        private void handleSubscribeResponse(long sequence, byte serializationType, byte[] body, int status) {
            if (status == ProtocolHeader.SUCCESS) {
                // deserialize
                Serializer serializer = SerializerFactory.getSerializer(serializationType);
                DispatchMeta dispatchMeta = serializer.readObject(body, DispatchMeta.class);
                // cache the service to local
                for (ServiceMeta meta : dispatchMeta.getServiceMetas()) {
                    localServiceContainer.put(meta);
                }

                synchronized (nonCompleteSubServices) {
                    nonCompleteSubServices.remove(sequence);
                    if (nonCompleteSubServices.size() == 0) {
                        // all service have subscribe successful
                        // notify the thread of subscribing service
                        consumer.timeout = false;
                        consumer.lock.lock();
                        consumer.notComplete.signalAll();
                        consumer.lock.unlock();
                    }
                }
            }
        }

        /**
         * Handle ACK from peer.
         */
        private void handleAcknowledge(long sequence) {
            logger.info("ack-receive; sequence:" + sequence);
            MessageNonAck ack = messagesNonAck.remove(sequence);
            logger.info("ack-remove; ack:" + ack);
        }

        @Override
        public void handleConnection(Channel channel) {
            DefaultRegistryService.this.channel = channel;
        }

        @Override
        public void handleDisconnection(Channel channel) {
            DefaultRegistryService.this.channel = null;
        }
    }
}
