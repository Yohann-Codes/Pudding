package org.pudding.rpc.consumer;

import org.apache.log4j.Logger;
import org.pudding.common.utils.Maps;
import org.pudding.transport.api.Channel;
import org.pudding.transport.protocol.Message;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Manager acknowledge.
 *
 * @author Yohann.
 */
public abstract class AcknowledgeManager {
    private static final Logger logger = Logger.getLogger(AcknowledgeManager.class);

    // Not receive ack at present
    protected final ConcurrentMap<Long, MessageNonAck> messagesNonAck = Maps.newConcurrentHashMap();

    public AcknowledgeManager() {
        Thread t = new Thread(new AckTimeoutWatchdog(), "ack-timeout-watchdog");
        t.setDaemon(true);
        t.start();
    }

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
