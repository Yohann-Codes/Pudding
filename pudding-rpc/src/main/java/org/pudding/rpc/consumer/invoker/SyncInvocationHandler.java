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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronous invocation handler.
 *
 * @author Yohann.
 */
public class SyncInvocationHandler extends Router implements InvocationHandler, InvocationComplete {
    private static final Logger logger = Logger.getLogger(SyncInvocationHandler.class);

    // the deadline of invoking a service
    private final int timeout;

    private final ExecutorService executor;

    private final ConcurrentMap<Long, RequestThread> requestThreads = Maps.newConcurrentHashMap();

    public SyncInvocationHandler(int timeout, LoadBalanceStrategy loadBalanceStrategy, ExecutorService executor) {
        super(loadBalanceStrategy);
        this.timeout = timeout;
        this.executor = executor;
    }

    @Override
    protected void initConnector(Connector connector) {
        connector.withProcessor(new SyncInvocationProcessor());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = proxy.getClass().getInterfaces()[0].getName();
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();

        // select a service
        InvocationPair invocationPair = route(serviceName);

        Channel channel = invocationPair.getChannel();
        Object serviceInstance = invocationPair.getServiceInstance();
        InvokeMeta invokeMeta = new InvokeMeta(serviceName, serviceInstance, methodName, paramTypes, args);

        return doInvoke(channel, invokeMeta);
    }

    /**
     * Start remote invocation.
     *
     * @param channel
     * @param invokeMeta
     */
    private Object doInvoke(final Channel channel, final InvokeMeta invokeMeta) throws InvokeTimeoutException {

        // generate sequence
        final long sequence = SequenceUtil.generateSequence();

        // create RequestThread and save it
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        RequestThread requestThread = new RequestThread(lock, condition);
        requestThreads.put(sequence, requestThread);

        executor.execute(new Runnable() {
            @Override
            public void run() {
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

        // blocking
        return waitCompleteInvocation(requestThread);
    }

    /**
     * Blocking current thread.
     */
    private Object waitCompleteInvocation(RequestThread requestThread) throws InvokeTimeoutException {
        Lock lock = requestThread.getLock();
        Condition condition = requestThread.getCondition();

        lock.lock();
        try {
            condition.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitCompleteInvocation()", e);
        } finally {
            lock.unlock();
        }

        synchronized (this) {
            boolean timeout = requestThread.isTimeout();
            if (timeout) {
                throw new InvokeTimeoutException("invocation timeout");
            }
            return requestThread.getResult();
        }
    }

    @Override
    public void completeInvocation(long sequence, ResultMeta resultMeta, int status) {

        if (status == ProtocolHeader.NOT_FIND_SERVICE) {
            throw new NotFindServiceException("not find service");
        }
        if (status == ProtocolHeader.SERVER_BUSY) {
            throw new ServiceBusyException("service busy");
        }
        if (status == ProtocolHeader.SUCCESS) {
            synchronized (requestThreads) {
                if (requestThreads.containsKey(sequence)) {
                    // set result
                    RequestThread requestThread = requestThreads.get(sequence);
                    requestThread.setTimeout(false);
                    requestThread.setResult(resultMeta.getResult());

                    // notify the requester thread
                    requestThread.getLock().lock();
                    requestThread.getCondition().signal();
                    requestThread.getLock().unlock();
                } else {
                    throw new NotFindServiceException("internal error");
                }
            }
        }

        // remove requester thread
        requestThreads.remove(sequence);
    }

    private class SyncInvocationProcessor implements Processor {

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
     * The information of requester thread.
     */
    private class RequestThread {
        private Lock lock;
        private Condition condition;
        private boolean timeout;

        private Object result;

        public RequestThread(Lock lock, Condition condition) {
            this.lock = lock;
            this.condition = condition;
            timeout = true;
        }

        public Lock getLock() {
            return lock;
        }

        public Condition getCondition() {
            return condition;
        }

        public boolean isTimeout() {
            return timeout;
        }

        public void setTimeout(boolean timeout) {
            this.timeout = timeout;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }
}