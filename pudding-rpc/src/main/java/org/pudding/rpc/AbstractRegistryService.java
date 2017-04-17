package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.Maps;
import org.pudding.transport.api.Channel;
import org.pudding.transport.protocol.Message;

import java.util.concurrent.*;

/**
 * Abstract registry_cluster service.
 *
 * @author Yohann.
 */
public abstract class AbstractRegistryService implements RegistryService {
    private static final Logger logger = Logger.getLogger(AbstractRegistryService.class);

    private final ExecutorService registerExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService subscribeExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<ServiceMeta> registerTaskQueue = new LinkedBlockingQueue<>(1024);
    private final BlockingQueue<ServiceMeta> subscribeTaskQueue = new LinkedBlockingQueue<>(1024);

    // Not receive ack at present
    protected final ConcurrentMap<Long, MessageNonAck> messagesNonAck = Maps.newConcurrentHashMap();

    // Control taskQueue
    private volatile boolean isShutdown = false;

    public AbstractRegistryService() {
        initRegisterQueue();
        initSubscribeQueue();

        Thread t = new Thread(new AckTimeoutWatchdog(), "ack-timeout-watchdog");
        t.setDaemon(true);
        t.start();
    }

    private void initRegisterQueue() {
        registerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown) {
                    ServiceMeta meta;
                    try {
                        meta = registerTaskQueue.take();
                        doRegister(meta);
                    } catch (InterruptedException e) {
                        logger.warn("take from taskQueue");
                    }
                }
            }
        });
    }

    private void initSubscribeQueue() {
        subscribeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown) {
                    ServiceMeta meta;
                    try {
                        meta = subscribeTaskQueue.take();
                        doSubscribe(meta);
                    } catch (InterruptedException e) {
                        logger.warn("take from taskQueue");
                    }
                }
            }
        });
    }

    @Override
    public void register(ServiceMeta serviceMeta) {
        try {
            registerTaskQueue.put(serviceMeta);
            logger.info("publish service: " + serviceMeta);
        } catch (InterruptedException e) {
            logger.warn("put service meta to taskQueue: " + serviceMeta);
        }
    }

    @Override
    public void unregister(ServiceMeta serviceMeta) {
        doUnregister(serviceMeta);
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta) {
        try {
            subscribeTaskQueue.put(serviceMeta);
            logger.info("subscribe service: " + serviceMeta);
        } catch (InterruptedException e) {
            logger.warn("put service meta to taskQueue: " + serviceMeta);
        }
    }

    @Override
    public void shutdown() {
        isShutdown = true;
        registerExecutor.shutdown();
        subscribeExecutor.shutdown();
    }

    protected void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    protected abstract void doRegister(ServiceMeta serviceMeta);

    protected abstract void doUnregister(ServiceMeta serviceMeta);

    protected abstract void doSubscribe(ServiceMeta serviceMeta);


    protected class MessageNonAck {
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

        @Override
        public String toString() {
            return "MessageNonAck{" +
                    "sequence=" + sequence +
                    ", timestamp=" + timestamp +
                    ", channel=" + channel +
                    ", message=" + message +
                    '}';
        }
    }

    protected class AckTimeoutWatchdog implements Runnable {

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

                                logger.info("ack timeout, rewrite message, channel:" + m.channel);
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
}
