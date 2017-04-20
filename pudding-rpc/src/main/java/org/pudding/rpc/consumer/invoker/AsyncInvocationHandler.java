package org.pudding.rpc.consumer.invoker;

import org.apache.log4j.Logger;
import org.pudding.common.constant.LoadBalanceStrategy;
import org.pudding.common.exception.InvokeTimeoutException;
import org.pudding.common.exception.NotFindServiceException;
import org.pudding.common.exception.ServiceBusyException;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ResultMeta;
import org.pudding.common.utils.Maps;
import org.pudding.common.utils.SequenceUtil;
import org.pudding.rpc.RpcConfig;
import org.pudding.rpc.consumer.invoker.future.InvokerFutureListener;
import org.pudding.rpc.consumer.router.Router;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.ChannelListener;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous invocation handler.
 *
 * @author Yohann.
 */
public class AsyncInvocationHandler extends Router implements InvocationHandler, InvocationComplete {
    private static final Logger logger = Logger.getLogger(AsyncInvocationHandler.class);

    // the deadline of invoking a service
    private final int timeout;

    private final ExecutorService executor;

    private final ConcurrentMap<Long, InvokerFutureListener> listeners = Maps.newConcurrentHashMap();

    public AsyncInvocationHandler(int timeout, LoadBalanceStrategy loadBalanceStrategy, ExecutorService executor) {
        super(loadBalanceStrategy);
        this.timeout = timeout;
        this.executor = executor;
    }

    @Override
    protected void initConnector(Connector connector) {
        connector.withProcessor(new AsyncInvocationProcessor());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = proxy.getClass().getInterfaces()[0].getName();
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();

        // generate sequence
        long sequence = SequenceUtil.generateSequence();
        try {
            listeners.put(sequence, (InvokerFutureListener) args[args.length - 1]);
        } catch (NullPointerException e) {
            // ignore
        }

        // select a service
        InvocationPair invocationPair = route(serviceName);

        Channel channel = invocationPair.getChannel();
        Object serviceInstance = invocationPair.getServiceInstance();
        InvokeMeta invokeMeta = new InvokeMeta(serviceName, serviceInstance, methodName, paramTypes, args);

        doInvoke(sequence, channel, invokeMeta);

        return defaultResult(method.getReturnType());
    }

    /**
     * Start remote invocation.
     *
     * @param channel
     * @param invokeMeta
     */
    private void doInvoke(final long sequence, final Channel channel, final InvokeMeta invokeMeta) throws InvokeTimeoutException {

        executor.execute(new Runnable() {

            @Override
            public void run() {

                // timout monitor
                new Timer().schedule(new TimeoutTask(sequence), TimeUnit.SECONDS.toMillis(timeout));

                byte serializationType = RpcConfig.getSerializationType();
                Serializer serializer = SerializerFactory.getSerializer(serializationType);
                byte[] body = serializer.writeObject(invokeMeta);

                ProtocolHeader header = new ProtocolHeader();
                header.setMagic(ProtocolHeader.MAGIC)
                        .setType(ProtocolHeader.type(serializationType, ProtocolHeader.REQUEST))
                        .setSign(ProtocolHeader.INVOKE_SERVICE)
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
                            logger.info("invoke-service-write success; serviceMeta:" + invokeMeta + "; channel:" + channel);

                            MessageNonAck messageNonAck = new MessageNonAck(sequence, channel, message);
                            messagesNonAck.put(sequence, messageNonAck);
                            logger.info("put-ack:" + messageNonAck);
                        }

                        @Override
                        public void operationFailure(Channel channel, Throwable cause) {
                            logger.warn("invoke-service-write failed; serviceMeta:" + invokeMeta + "; channel:" + channel);
                        }
                    });
                } else {
                    logger.warn("invoke-service-write failed, channel is not active; serviceMeta:" + invokeMeta + "; channel:" + channel);
                }
            }
        });
    }

    /**
     * The default result.
     *
     * @param returnType
     */
    private Object defaultResult(Class<?> returnType) {
        String name = returnType.getName();
        Object result;
        switch (name) {
            case "boolean":
                result = DefaultValue.BOOLEAN_VALUE;
                break;
            case "byte":
                result = DefaultValue.BYTE_VALUE;
                break;
            case "short":
                result = DefaultValue.SHORT_VALUE;
                break;
            case "int":
                result = DefaultValue.INT_VALUE;
                break;
            case "long":
                result = DefaultValue.LONG_VALUE;
                break;
            case "float":
                result = DefaultValue.FLOAT_VALUE;
                break;
            case "double":
                result = DefaultValue.DOUBLE_VALUE;
                break;
            default:
                result = null;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void completeInvocation(long sequence, ResultMeta resultMeta, int status) {
        synchronized (listeners) {
            if (listeners.containsKey(sequence)) {
                InvokerFutureListener listener = listeners.get(sequence);

                if (status == ProtocolHeader.NOT_FIND_SERVICE) {
                    listener.failure(new NotFindServiceException("not find service"));
                }
                if (status == ProtocolHeader.SERVER_BUSY) {
                    listener.failure(new ServiceBusyException("service busy"));
                }
                if (status == ProtocolHeader.SUCCESS) {
                    listener.success(resultMeta.getResult());
                }

                // remove listener
                listeners.remove(sequence);
            }
        }
    }

    private class AsyncInvocationProcessor implements Processor {

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
                        case ProtocolHeader.RESPONSE:
                            switch (sign) {
                                case ProtocolHeader.INVOKE_SERVICE:
                                    handleInvocationResponse(sequence, body, serializationType, status);
                                    break;
                            }

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
         * Handle invocation response.
         */
        private void handleInvocationResponse(long sequence, byte[] body, byte serializationType, int status) {
            Serializer serializer = SerializerFactory.getSerializer(serializationType);
            ResultMeta resultMeta = serializer.readObject(body, ResultMeta.class);
            completeInvocation(sequence, resultMeta, status);
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
            // Noop
        }

        @Override
        public void handleDisconnection(Channel channel) {
            // Noop
        }
    }

    /**
     * The default value.
     */
    private class DefaultValue {
        public static final boolean BOOLEAN_VALUE = false;
        public static final byte BYTE_VALUE = (byte) 0;
        public static final short SHORT_VALUE = (short) 0;
        public static final int INT_VALUE = 0;
        public static final float FLOAT_VALUE = (float) 0.0;
        public static final long LONG_VALUE = 0;
        public static final double DOUBLE_VALUE = 0.0;
    }

    private class TimeoutTask extends TimerTask {
        private long sequence;

        public TimeoutTask(long sequence) {
            this.sequence = sequence;
        }

        @Override
        public void run() {
            if (listeners.containsKey(sequence)) {
                listeners.get(sequence).failure(new InvokeTimeoutException("invocation timeout"));
            }
        }
    }
}
