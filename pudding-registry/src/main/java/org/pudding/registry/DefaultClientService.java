package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.common.utils.SequenceUtil;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.ChannelListener;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;
import org.pudding.transport.protocol.ProtocolHeader;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * The default implementation of {@link ClientService}.
 *
 * @author Yohann.
 */
public class DefaultClientService extends AcknowledgeManager implements ClientService {
    private static final Logger logger = Logger.getLogger(DefaultClusterService.class);

    // Process the client(provider/consumer) task
    private final Processor clientProcessor = new ClientProcessor();

    private final Acceptor acceptor = NettyTransportFactory.createTcpAcceptor();

    private final ServiceRegContainer serviceRegContainer = new ServiceRegContainer();

    private final ConcurrentMap<Long, Channel> nonSyncPublishServices = Maps.newConcurrentHashMap();
    private final ConcurrentMap<Long, Channel> nonSyncUnpublishServices = Maps.newConcurrentHashMap();

    private ClusterService clusterService;

    private Channel channel;

    private volatile boolean isShutdown = false;

    private final ExecutorService executor;

    public DefaultClientService(ExecutorService executor) {
        acceptor.withProcessor(clientProcessor);
        this.executor = executor;
    }

    @Override
    public Channel startRegistry(SocketAddress localAddress) {
        checkNotShutdown();

        try {
            synchronized (acceptor) {
                channel = acceptor.bind(localAddress);

                logger.info("start registry server, channel:" + channel);
                return channel;
            }
        } catch (InterruptedException e) {
            logger.warn("start registry failed: " + channel, e);
        }

        return null; // Never get here
    }

    @Override
    public void closeRegistry() {
        channel.close();

        logger.info("close registry, channel:" + channel);
        channel = null;
    }

    @Override
    public void registerService(ServiceMeta serviceMeta) {
        serviceRegContainer.put(serviceMeta);
        logger.info("register-service success; serviceMeta:" + serviceMeta);
        logger.info(serviceRegContainer);
    }

    @Override
    public void unregisterService(ServiceMeta serviceMeta) {
        serviceRegContainer.remove(serviceMeta);
        logger.info("unregister-service success; serviceMeta:" + serviceMeta);
        logger.info(serviceRegContainer);
    }

    @Override
    public void withClusterService(ClusterService service) {
        clusterService = service;
    }

    @Override
    public void shutdown() {
        closeRegistry();
        acceptor.shutdownGracefully();
        isShutdown = true;
    }

    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    /**
     * The processor about client(provider/consumer).
     */
    private class ClientProcessor implements Processor {

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

                    switch (messageType) {
                        case ProtocolHeader.REQUEST:
                            // Reply ack
                            replyAcknowledge(channel, sequence);

                            switch (sign) {
                                case ProtocolHeader.PUBLISH_SERVICE:
                                    handlePublishService(sequence, channel, serializationType, body);
                                    break;
                                case ProtocolHeader.UNPUBLISH_SERVICE:
                                    handleUnpublishService(sequence, channel, serializationType, body);
                                default:
                                    logger.warn("invalid sign: " + sign);
                            }
                            break;

                        case ProtocolHeader.CLUSTER_SYNC:
                            // Reply ack
                            replyAcknowledge(channel, sequence);

                            switch (sign) {
                                case ProtocolHeader.PUBLISH_SERVICE:
                                    handleClusterPublishService(sequence, serializationType, body);
                                    break;
                                case ProtocolHeader.UNPUBLISH_SERVICE:
                                    handleClusterUnpublishService(sequence, serializationType, body);
                                    break;
                                default:
                                    logger.warn("invalid sign: " + sign);
                            }
                            break;

                        case ProtocolHeader.RESPONSE:
                            break;

                        case ProtocolHeader.ACK:
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
         * Handle publish service.
         */
        private void handlePublishService(long sequence, Channel channel, byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            // register service
            registerService(serviceMeta);

            // save origin registry server
            long originId = sequence;
            nonSyncPublishServices.put(originId, channel);

            clusterService.servicePublishSync(originId, serviceMeta);
        }

        /**
         * Handle unpublish service.
         */
        private void handleUnpublishService(long sequence, Channel channel, byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            // unregister service
            unregisterService(serviceMeta);

            // save origin registry server
            long originId = sequence;
            nonSyncUnpublishServices.put(originId, channel);

            clusterService.serviceUnpublishSync(originId, serviceMeta);
        }

        /**
         * Handle publish service of cluster.
         */
        private void handleClusterPublishService(final long sequence, byte serializationType, byte[] body) {
            if (nonSyncPublishServices.containsKey(sequence)) {
                // is the origin registry server
                // response the client
                Channel channel = nonSyncPublishServices.get(sequence);
                nonSyncPublishServices.remove(sequence);

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type((byte) 0, ProtocolHeader.RESPONSE))
                        .setSign(ProtocolHeader.PUBLISH_SERVICE)
                        .setSequence(sequence)
                        .setStatus(ProtocolHeader.SUCCESS)
                        .setBodyLength(0);

                Message message = new Message();
                message.setHeader(header)
                        .setBody(new byte[0]);

                if (channel.isActive()) {
                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("response-publish-write success; sequence:" + sequence + "; channel:" + channel);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("response-publish-write failed; sequence:" + sequence + "; channel:" + channel, cause);
                        }
                    });
                } else {
                    logger.warn("response-publish-write failed, channel is not active; sequence:" + sequence + "; channel:" + channel);
                }

                return;
            }

            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            // register service
            registerService(serviceMeta);

            long originId = sequence;
            clusterService.servicePublishSync(originId, serviceMeta);
        }

        /**
         * Handle unpublish service of cluster.
         */
        private void handleClusterUnpublishService(final long sequence, byte serializationType, byte[] body) {
            if (nonSyncUnpublishServices.containsKey(sequence)) {
                // is the origin registry server
                // response the client
                Channel channel = nonSyncUnpublishServices.get(sequence);
                nonSyncUnpublishServices.remove(sequence);

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type((byte) 0, ProtocolHeader.RESPONSE))
                        .setSign(ProtocolHeader.UNPUBLISH_SERVICE)
                        .setSequence(sequence)
                        .setStatus(ProtocolHeader.SUCCESS)
                        .setBodyLength(0);

                Message message = new Message();
                message.setHeader(header)
                        .setBody(new byte[0]);

                if (channel.isActive()) {
                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("response-unpublish-write success; sequence:" + sequence + "; channel:" + channel);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("response-unpublish-write failed; sequence:" + sequence + "; channel:" + channel, cause);
                        }
                    });
                } else {
                    logger.warn("response-unpublish-write failed, channel is not active; sequence:" + sequence + "; channel:" + channel);
                }

                return;
            }

            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            // register service
            unregisterService(serviceMeta);

            long originId = sequence;
            clusterService.serviceUnpublishSync(originId, serviceMeta);
        }


        @Override
        public void handleConnection(Channel channel) {
            // Noop
        }

        @Override
        public void handleDisconnection(Channel channel) {
            // Noop
        }
    }
}
