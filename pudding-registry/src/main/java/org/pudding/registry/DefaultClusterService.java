package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.SubscriberMeta;
import org.pudding.common.utils.SequenceUtil;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.ChannelListener;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTcpConnector;
import org.pudding.transport.netty.NettyTransportFactory;
import org.pudding.transport.protocol.ProtocolHeader;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * The default implementation of {@link ClusterService}.
 *
 * @author Yohann.
 */
public class DefaultClusterService extends AcknowledgeManager implements ClusterService {
    private static final Logger logger = Logger.getLogger(DefaultClusterService.class);

    // Process the cluster task
    private final Processor clusterProcessor = new RegistryProcessor();

    private final Connector connector = NettyTransportFactory.createTcpConnector();

    private volatile Channel channel;

    private ClientService clientService;

    private volatile boolean isShutdown = false;

    private final ExecutorService executor;

    public DefaultClusterService(ExecutorService executor) {
        connector.withProcessor(clusterProcessor);
        this.executor = executor;
    }

    @Override
    public void connectCluster(SocketAddress... prevAddress) {
        checkNotShutdown();

        try {
            synchronized (connector) {
                // Connect to last server of cluster
                channel = connector.connect(NettyTcpConnector.ReconnPattern.CONNECT_PREVIOUS_ADDRESS, prevAddress);
                logger.info("connect with registry cluster server, channel: " + channel);
            }
        } catch (InterruptedException e) {
            logger.warn("connect with registry cluster server failed, channel: " + channel);
        }
    }

    @Override
    public void disconnectCluster() {
        if (channel != null) {
            channel.close();
            logger.info("disconnect with registry cluster server, channel:" + channel);
        }
    }

    @Override
    public void servicePublishSync(long originId, final ServiceMeta serviceMeta) {
        byte serializationType = RegistryConfig.getSerializationType();
        Serializer serializer = SerializerFactory.getSerializer(serializationType);
        byte[] body = serializer.writeObject(serviceMeta);

        final long sequence = originId;

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC)
                .setType(ProtocolHeader.type(serializationType, ProtocolHeader.CLUSTER_SYNC))
                .setSign(ProtocolHeader.PUBLISH_SERVICE)
                .setSequence(sequence)
                .setStatus(0)
                .setBodyLength(body.length);

        final Message message = new Message();
        message.setHeader(header)
                .setBody(body);

        if (channel != null) {
            if (channel.isActive()) {
                channel.write(message, new ChannelListener() {
                    @Override
                    public void operationSuccess(Channel channel) {
                        logger.info("sync-publish-service-write success; serviceMeta:" + serviceMeta + "; channel:" + channel);

                        MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                        messagesNonAck.put(sequence, messageNonAck);

                        logger.info("put-ack:" + messageNonAck);
                    }

                    @Override
                    public void operationFailure(Channel channel, Throwable cause) {
                        logger.warn("sync-publish-service-write failed; serviceMeta:" + serviceMeta + "; channel:" + channel);
                    }
                });
            } else {
                logger.warn("sync-publish-service-write failed: channel is not active; serviceMeta:" + serviceMeta + ", channel:" + channel);
            }
        } else {
            logger.warn("sync-publish-service-write failed: channel is null; serviceMeta:" + serviceMeta);
        }
    }

    @Override
    public void serviceUnpublishSync(long originId, final ServiceMeta serviceMeta) {
        byte serializationType = RegistryConfig.getSerializationType();
        Serializer serializer = SerializerFactory.getSerializer(serializationType);
        byte[] body = serializer.writeObject(serviceMeta);

        final long sequence = originId;

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC)
                .setType(ProtocolHeader.type(serializationType, ProtocolHeader.CLUSTER_SYNC))
                .setSign(ProtocolHeader.UNPUBLISH_SERVICE)
                .setSequence(sequence)
                .setStatus(0)
                .setBodyLength(body.length);

        final Message message = new Message();
        message.setHeader(header)
                .setBody(body);

        if (channel != null) {
            if (channel.isActive()) {
                channel.write(message, new ChannelListener() {
                    @Override
                    public void operationSuccess(Channel channel) {
                        logger.info("sync-subscribe-service-write success; serviceMeta:" + serviceMeta + "; channel:" + channel);

                        MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                        messagesNonAck.put(sequence, messageNonAck);

                        logger.info("put-ack:" + messageNonAck);
                    }

                    @Override
                    public void operationFailure(Channel channel, Throwable cause) {
                        logger.warn("sync-subscribe-service-write failed; serviceMeta:" + serviceMeta + "; channel:" + channel);
                    }
                });
            } else {
                logger.warn("sync-subscribe-service-write failed: channel is not active; serviceMeta:" + serviceMeta + ", channel:" + channel);
            }
        } else {
            logger.warn("sync-subscribe-service-write failed: channel is null; serviceMeta:" + serviceMeta);
        }
    }

    @Override
    public void serviceSubscribeSync(long originId, final SubscriberMeta subscriberMeta) {
        byte serializationType = RegistryConfig.getSerializationType();
        Serializer serializer = SerializerFactory.getSerializer(serializationType);
        byte[] body = serializer.writeObject(subscriberMeta);

        final long sequence = originId;

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC)
                .setType(ProtocolHeader.type(serializationType, ProtocolHeader.CLUSTER_SYNC))
                .setSign(ProtocolHeader.SUBSCRIBE_SERVICE)
                .setSequence(sequence)
                .setStatus(0)
                .setBodyLength(body.length);

        final Message message = new Message();
        message.setHeader(header)
                .setBody(body);

        if (channel != null) {
            if (channel.isActive()) {
                channel.write(message, new ChannelListener() {
                    @Override
                    public void operationSuccess(Channel channel) {
                        logger.info("sync-subscribe-service-write success; subscriberMeta:" + subscriberMeta + "; channel:" + channel);

                        MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                        messagesNonAck.put(sequence, messageNonAck);

                        logger.info("put-ack:" + messageNonAck);
                    }

                    @Override
                    public void operationFailure(Channel channel, Throwable cause) {
                        logger.warn("sync-subscribe-service-write failed; subscriberMeta:" + subscriberMeta + "; channel:" + channel);
                    }
                });
            } else {
                logger.warn("sync-subscribe-service-write failed: channel is not active; subscriberMeta:" + subscriberMeta + ", channel:" + channel);
            }
        } else {
            logger.warn("sync-subscribe-service-write failed: channel is null; subscriberMeta:" + subscriberMeta);
        }
    }

    @Override
    public void withClientService(ClientService service) {
        clientService = service;
    }

    @Override
    public void shutdown() {
        disconnectCluster();
        connector.shutdownGracefully();
        isShutdown = true;
    }

    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    /**
     * The processor about cluster.
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

                    switch (messageType) {
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
         * Handle ACK from peer.
         */
        private void handleAcknowledge(long sequence) {
            logger.info("ack-receive; sequence:" + sequence);
            MessageNonAck ack = messagesNonAck.remove(sequence);
            logger.info("ack-remove; ack:" + ack);
        }

        @Override
        public void handleConnection(Channel channel) {
            DefaultClusterService.this.channel = channel;
        }

        @Override
        public void handleDisconnection(Channel channel) {
            DefaultClusterService.this.channel = null;
        }
    }
}
