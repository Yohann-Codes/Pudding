package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.common.utils.SequenceUtil;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    // Registry channel
    private volatile Channel channel;
    private volatile boolean isConnected = false;

    private final ExecutorService executor;
    private final DefaultServiceProvider provider;

    public DefaultRegistryService(DefaultServiceProvider provider, ExecutorService executor) {
        connector.withProcessor(registryProcessor);
        this.provider = provider;
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
    protected void doSubscribe(ServiceMeta serviceMeta) {

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
        public void handleMessage(final Channel channel, final Message holder) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    ProtocolHeader header = holder.getHeader();
                    byte[] body = holder.getBody();

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
                            break;

                        case ProtocolHeader.RESPONSE:
                            switch (sign) {
                                case ProtocolHeader.PUBLISH_SERVICE:
                                    handlePublishResponse(sequence, status);
                                    break;
                                case ProtocolHeader.UNPUBLISH_SERVICE:
                                    handleUnpublishResponse(status);
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
