package org.pudding.common.utils;

import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;

/**
 * MessageHolder Factory.
 *
 * @author Yohann.
 */
public class MessageHolderFactory {

    /**
     * Create Heartbeat MessageHolder.
     */
    public static MessageHolder newHeartbeatHolder() {
        ProtocolHeader header = ProtocolHeaderFactory.newHeartbeatHeader();
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(new byte[]{});
        return holder;
    }

    /**
     * Create PublishService MessageHolder.
     */
    public static MessageHolder newPublishServiceHolder(byte[] body) {
        ProtocolHeader header = ProtocolHeaderFactory.newPublishServiceHeader(body.length);
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create UnpublishService MessageHolder.
     */
    public static MessageHolder newUnpublishServiceHolder(byte[] body) {
        ProtocolHeader header = ProtocolHeaderFactory.newUnpublishServiceHeader(body.length);
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }
}
