package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.model.DispatchMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.SubscriberMeta;
import org.pudding.common.utils.AddressUtil;
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
import java.util.List;
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

    private final RegistryContainer registryContainer = new RegistryContainer();
    private final SubscriberContainer subscriberContainer = new SubscriberContainer();

    private final ConcurrentMap<Long, Channel> nonSyncPublishServices = Maps.newConcurrentHashMap();
    private final ConcurrentMap<Long, Channel> nonSyncUnpublishServices = Maps.newConcurrentHashMap();
    private final ConcurrentMap<Long, Channel> nonSyncSubscribeServices = Maps.newConcurrentHashMap();

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
        registryContainer.put(serviceMeta);
        logger.info("register-service success; serviceMeta:" + serviceMeta);
        logger.info(registryContainer);
    }

    @Override
    public void unregisterService(ServiceMeta serviceMeta) {
        registryContainer.remove(serviceMeta);
        logger.info("unregister-service success; serviceMeta:" + serviceMeta);
        logger.info(registryContainer);
    }

    public boolean subscribeService(ServiceMeta serviceMeta, Channel channel) {

        // look up service
        List<ServiceMeta> services = registryContainer.get(serviceMeta.getName());
        if (services != null) {
            // save subscriber
            String name = serviceMeta.getName();
            String host = AddressUtil.host(channel.remoteAddress().toString());
            subscriberContainer.putHost(name, host);
            subscriberContainer.putChannel(name, channel);
            return true;
        }

        return false;
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
                        case ProtocolHeader.REQUEST:
                            // Reply ack
                            replyAcknowledge(channel, sequence);

                            switch (sign) {
                                case ProtocolHeader.PUBLISH_SERVICE:
                                    handlePublishService(sequence, channel, serializationType, body);
                                    break;
                                case ProtocolHeader.UNPUBLISH_SERVICE:
                                    handleUnpublishService(sequence, channel, serializationType, body);
                                    break;
                                case ProtocolHeader.SUBSCRIBE_SERVICE:
                                    handleSubscribeService(sequence, channel, serializationType, body);
                                    break;
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
                                case ProtocolHeader.SUBSCRIBE_SERVICE:
                                    handleClusterSubscribeService(sequence, serializationType, body);
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
         * Handle subscribe service.
         */
        private void handleSubscribeService(long sequence, Channel channel, byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            // subscribe service
            boolean subscribe = subscribeService(serviceMeta, channel);

            if (subscribe) {
                // subscribe successful
                // cluster sync
                // save origin regsitry server
                long originId = sequence;
                String host = AddressUtil.host(channel.remoteAddress().toString());
                SubscriberMeta subscriberMeta = new SubscriberMeta(serviceMeta.getName(), host);
                nonSyncSubscribeServices.put(originId, channel);
                clusterService.serviceSubscribeSync(originId, subscriberMeta);
            } else {
                // subscribe failed
                // do nothing, waiting for the client timeout
            }
        }

        /**
         * Handle publish service of cluster.
         */
        private void handleClusterPublishService(final long sequence, byte serializationType, byte[] body) {

            // dispatch service to subscribers
            dispatchService(serializationType, body);

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
         * Dispatch service to subscribers.
         */
        private void dispatchService(byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            List<Channel> channels = subscriberContainer.getChannels(serviceMeta.getName());
            if (channels != null && channels.size() > 0) {
                for (Channel channel : channels) {
                    serializationType = RegistryConfig.getSerializationType();
                    serializer = SerializerFactory.getSerializer(serializationType);
                    body = serializer.writeObject(serviceMeta);

                    final long sequence = SequenceUtil.generateSequence();

                    ProtocolHeader header = new ProtocolHeader();
                    header.setMagic(ProtocolHeader.MAGIC)
                            .setType(ProtocolHeader.type(RegistryConfig.getSerializationType(), ProtocolHeader.REQUEST))
                            .setSign(ProtocolHeader.DISPATCH_SERVICE)
                            .setSequence(sequence)
                            .setStatus(0)
                            .setBodyLength(body.length);

                    final Message message = new Message();
                    message.setHeader(header)
                            .setBody(body);

                    if (channel.isActive()) {
                        channel.write(message, new ChannelListener() {
                            @Override
                            public void operationSuccess(Channel channel) {
                                logger.info("dispatch-service-write success; sequence:" + sequence + "; channel:" + channel);

                                MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                                messagesNonAck.put(sequence, messageNonAck);
                                logger.info("put-ack:" + messageNonAck);
                            }

                            @Override
                            public void operationFailure(Channel channel, Throwable cause) {
                                logger.warn("dispatch-service-write failed; sequence:" + sequence + "; channel:" + channel, cause);
                            }
                        });
                    } else {
                        logger.warn("dispatch-service-write failed, channel is not active; sequence:" + sequence + "; channel:" + channel);
                    }
                }
            }
        }

        /**
         * Handle unpublish service of cluster.
         */
        private void handleClusterUnpublishService(final long sequence, byte serializationType, byte[] body) {

            // notice subscribers that service offline
            noticeOffline(serializationType, body);

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

        /**
         * Notice subscribers that service offline.
         */
        private void noticeOffline(byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);

            List<Channel> channels = subscriberContainer.getChannels(serviceMeta.getName());
            if (channels != null && channels.size() > 0) {
                for (Channel channel : channels) {
                    serializationType = RegistryConfig.getSerializationType();
                    serializer = SerializerFactory.getSerializer(serializationType);
                    body = serializer.writeObject(serviceMeta);

                    final long sequence = SequenceUtil.generateSequence();

                    ProtocolHeader header = new ProtocolHeader();
                    header.setMagic(ProtocolHeader.MAGIC)
                            .setType(ProtocolHeader.type(RegistryConfig.getSerializationType(), ProtocolHeader.REQUEST))
                            .setSign(ProtocolHeader.OFFLINE_SERVICE)
                            .setSequence(sequence)
                            .setStatus(0)
                            .setBodyLength(body.length);

                    final Message message = new Message();
                    message.setHeader(header)
                            .setBody(body);

                    if (channel.isActive()) {
                        channel.write(message, new ChannelListener() {
                            @Override
                            public void operationSuccess(Channel channel) {
                                logger.info("notice-service-offline-write success; sequence:" + sequence + "; channel:" + channel);

                                MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                                messagesNonAck.put(sequence, messageNonAck);
                                logger.info("put-ack:" + messageNonAck);
                            }

                            @Override
                            public void operationFailure(Channel channel, Throwable cause) {
                                logger.warn("notice-service-offline-write failed; sequence:" + sequence + "; channel:" + channel, cause);
                            }
                        });
                    } else {
                        logger.warn("notice-service-offline-write failed, channel is not active; sequence:" + sequence + "; channel:" + channel);
                    }
                }
            }
        }

        /**
         * Handle subscribe service of cluster.
         */
        private void handleClusterSubscribeService(final long sequence, byte serializationType, byte[] body) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            SubscriberMeta subscriberMeta = serializer.readObject(body, SubscriberMeta.class);

            if (nonSyncSubscribeServices.containsKey(sequence)) {
                // is the origin registry server
                // response the client
                Channel channel = nonSyncSubscribeServices.get(sequence);
                nonSyncSubscribeServices.remove(sequence);

                String serviceName = subscriberMeta.getServiceName();
                List<ServiceMeta> metaList = registryContainer.get(serviceName);
                if (metaList == null) {
                    return;
                }

                DispatchMeta dispatchMeta = new DispatchMeta(serviceName, metaList);

                serializationType = RegistryConfig.getSerializationType();
                serializer = SerializerFactory.getSerializer(serializationType);
                body = serializer.writeObject(dispatchMeta);

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type(RegistryConfig.getSerializationType(), ProtocolHeader.RESPONSE))
                        .setSign(ProtocolHeader.SUBSCRIBE_SERVICE)
                        .setSequence(sequence)
                        .setStatus(ProtocolHeader.SUCCESS)
                        .setBodyLength(body.length);

                Message message = new Message();
                message.setHeader(header)
                        .setBody(body);

                if (channel.isActive()) {
                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("response-subscribe-write success; sequence:" + sequence + "; channel:" + channel);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("response-subscribe-write failed; sequence:" + sequence + "; channel:" + channel, cause);
                        }
                    });
                } else {
                    logger.warn("response-subscribe-write failed, channel is not active; sequence:" + sequence + "; channel:" + channel);
                }

                return;
            }

            // save subscriber's host
            subscriberContainer.putHost(subscriberMeta.getServiceName(), subscriberMeta.getSubscriberHost());

            long originId = sequence;
            clusterService.serviceSubscribeSync(originId, subscriberMeta);
        }

        @Override
        public void handleConnection(final Channel channel) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // check for subscribers
                    String host = AddressUtil.host(channel.remoteAddress().toString());
                    List<String> serviceNameList = subscriberContainer.getServiceName(host);
                    for (String serviceName : serviceNameList) {
                        subscriberContainer.putChannel(serviceName, channel);
                    }
                }
            });
        }

        @Override
        public void handleDisconnection(final Channel channel) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // clear subscribe channel
                    String host = AddressUtil.host(channel.remoteAddress().toString());
                    List<String> serviceNameList = subscriberContainer.getServiceName(host);
                    for (String serviceName : serviceNameList) {
                        subscriberContainer.clearChannel(serviceName);
                    }
                }
            });
        }
    }
}