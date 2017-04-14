package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.rpc.provider.config.ProviderConfig;
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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

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

    // Not receive ack at present
    private final ConcurrentMap<Long, MessageNonAck> messagesNonAck = Maps.newConcurrentHashMap();

    // Registry channel
    private volatile Channel channel;
    private volatile boolean isConnected = false;

    public DefaultRegistryService() {
        connector.withProcessor(registryProcessor);
        Thread t = new Thread(new AckTimeoutWatchdog(), "ack-timeout-watchdog");
        t.setDaemon(true);
        t.start();
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
        byte serializerType = ProviderConfig.getSerializerType();
        Serializer serializer = SerializerFactory.getSerializer(serializerType);
        byte[] body = serializer.writeObject(serviceMeta);

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC)
                .setType(ProtocolHeader.type(serializerType, ProtocolHeader.REQUEST))
                .setSign(ProtocolHeader.PUBLISH_SERVICE)
                .setInvokeId(0)
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
                    logger.info("write success, channel:" + channel + ", " + serviceMeta);

                    MessageNonAck messageNonAck = new MessageNonAck(message.getSequence(), channel, message);
                    messagesNonAck.put(message.getSequence(), messageNonAck);

                    // ------------------
                    System.out.println("debug: " + messagesNonAck);
                }

                @Override
                public void operationFailure(Channel channel, Throwable cause) {
                    logger.warn("write failed, channel:" + channel + ", " + serviceMeta);
                }
            });
        } else {
            logger.warn("write failed, channel is not active, channel:" + channel + ", " + serviceMeta);
        }
    }

    @Override
    protected void doUnregister(ServiceMeta serviceMeta) {

    }

    @Override
    protected void doSubscribe(ServiceMeta serviceMeta) {

    }

    @Override
    public void shutdown() {
        super.shutdown();
        connector.shutdownGracefully();
    }

    private class MessageNonAck {
        private final long sequence;
        private final long timestamp;
        private final Channel channel;
        private final Message message;

        public MessageNonAck(long sequence, Channel channel, Message message) {
            timestamp = System.currentTimeMillis();
            this.sequence = sequence;
            this.channel = channel;
            this.message = message;
        }
    }

    private class AckTimeoutWatchdog implements Runnable {

        @Override
        public void run() {
            for (; ; ) {
                try {
                    for (MessageNonAck m : messagesNonAck.values()) {
                        if (System.currentTimeMillis() - m.timestamp > TimeUnit.SECONDS.toMillis(5)) {

                            // remove, need new timestamp
                            if (messagesNonAck.remove(m.sequence) == null) {
                                continue;
                            }

                            if (m.channel.isActive()) {
                                MessageNonAck msgNonAck = new MessageNonAck(m.sequence, m.channel, m.message);
                                messagesNonAck.put(msgNonAck.sequence, msgNonAck);
                                m.channel.write(m.message);
                            }
                        }
                    }

                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    logger.error("ack timeout watchdog error", e);
                }
            }
        }
    }

    /**
     * The processor about registry_cluster.
     */
    private class RegistryProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }

        @Override
        public void handleConnection(Channel channel) {
            DefaultRegistryService.this.channel = channel;
        }

        @Override
        public void handleDisconnection(Channel channel) {

        }
    }
}
