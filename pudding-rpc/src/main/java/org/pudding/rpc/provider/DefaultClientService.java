package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ResultMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.AddressUtil;
import org.pudding.rpc.RpcConfig;
import org.pudding.rpc.provider.flow_control.FlowController;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelListener;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * The default implementation of {@link ClientService}.
 *
 * @author Yohann.
 */
public class DefaultClientService implements ClientService {
    private static final Logger logger = Logger.getLogger(DefaultClientService.class);

    // Process the client(consumer) task
    private final Processor clientProcessor = new ClientProcessor();

    private final Acceptor acceptor = NettyTransportFactory.createTcpAcceptor();

    private volatile boolean isShutdown = false;

    private final ExecutorService executor;
    private final FlowController flowController;

    public DefaultClientService(ExecutorService executor) {
        acceptor.withProcessor(clientProcessor);
        this.executor = executor;
        flowController = new FlowController();
    }

    @Override
    public Channel startService(ServiceMeta serviceMeta) {
        checkNotShutdown();

        String address = serviceMeta.getAddress();
        AddressUtil.checkFormat(address);
        int port = AddressUtil.port(address);

        Channel channel;
        try {
            channel = acceptor.bind(port);
            logger.info("start service; channel:" + channel);
            logger.info("start service: " + serviceMeta);
            return channel;
        } catch (InterruptedException e) {
            logger.warn("start service failed: " + serviceMeta, e);
        }

        return null; // Never get here
    }

    @Override
    public void stopService(ServiceMeta serviceMeta, Channel channel) {
        channel.close();
        logger.info("stop service, channel:" + channel);
        logger.info("stop service: " + serviceMeta);
    }

    @Override
    public void shutdown() {
        acceptor.shutdownGracefully();
        isShutdown = true;
    }

    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    /**
     * The processor about client(consumer).
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
                                case ProtocolHeader.INVOKE_SERVICE:
                                    handleInvokeService(sequence, channel, serializationType, body);
                                    break;
                                default:
                                    logger.warn("invalid sign: " + sign);
                            }
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
         * Handle invocation.
         */
        private void handleInvokeService(final long sequence, Channel channel, byte serializationType, byte[] body) {
            // check the flow
            if (flowController.overFlowThreshold()) {
                serviceBusy(sequence, channel);
                return;
            }

            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            InvokeMeta invokeMeta = serializer.readObject(body, InvokeMeta.class);

            int status = ProtocolHeader.SUCCESS;
            final ResultMeta resultMeta = new ResultMeta(null);

            String serviceName = invokeMeta.getServiceName();
            Object serviceInstance = invokeMeta.getServiceInstance();
            String methodName = invokeMeta.getMethodName();
            Class<?>[] paramTypes = invokeMeta.getParamTypes();
            Object[] params = invokeMeta.getParams();
            try {
                Class<?> service = Class.forName(serviceName);
                Method method = service.getMethod(methodName, paramTypes);
                Object result = method.invoke(serviceInstance, params);
                resultMeta.setResult(result);
            } catch (Exception e) {
                // Not find service
                status = ProtocolHeader.NOT_FIND_SERVICE;
            } finally {
                // send response
                if (channel.isActive()) {
                    serializationType = RpcConfig.getSerializationType();
                    serializer = SerializerFactory.getSerializer(serializationType);
                    body = serializer.writeObject(resultMeta);

                    ProtocolHeader header = new ProtocolHeader();
                    header.setMagic(ProtocolHeader.MAGIC)
                            .setType(ProtocolHeader.type(serializationType, ProtocolHeader.RESPONSE))
                            .setSign(ProtocolHeader.INVOKE_SERVICE)
                            .setSequence(sequence)
                            .setStatus(status)
                            .setBodyLength(body.length);

                    final Message message = new Message();
                    message.setHeader(header)
                            .setBody(body);

                    channel.write(message, new ChannelListener() {
                        @Override
                        public void operationSuccess(Channel channel) {
                            logger.info("invoke-response-write success; resultMeta:" + resultMeta + "; channel:" + channel);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("invoke-response-write failed; resultMeta:" + resultMeta + "; channel:" + channel);
                        }
                    });
                } else {
                    logger.warn("invoke-response-write failed, channel is not active; resultMeta:" + resultMeta + "; channel:" + channel);
                }
            }
        }

        /**
         * Over flow threshold。
         */
        private void serviceBusy(long sequence, Channel channel) {
            if (channel.isActive()) {
                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type((byte) 0, ProtocolHeader.RESPONSE))
                        .setSign(ProtocolHeader.INVOKE_SERVICE)
                        .setSequence(sequence)
                        .setStatus(ProtocolHeader.SERVER_BUSY)
                        .setBodyLength(0);

                final Message message = new Message();
                message.setHeader(header)
                        .setBody(new byte[0]);

                channel.write(message);
            }
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
