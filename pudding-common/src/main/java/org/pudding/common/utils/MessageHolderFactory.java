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
     * Create PublishService Request MessageHolder.
     */
    public static MessageHolder newPublishServiceRequestHolder(byte[] body, byte serializerType) {
        ProtocolHeader header = ProtocolHeaderFactory.newPublishServiceRequestHeader(body.length, serializerType);
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create PublishService Response MessageHolder.
     */
    public static MessageHolder newPublishServiceResponseHolder(byte[] body, byte serializerType, int resultCode) {
        ProtocolHeader header = ProtocolHeaderFactory.newPublishServiceResponseHeader(body.length, serializerType, resultCode);
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create SubscribeService Request MessageHolder.
     */
    public static MessageHolder newSubscribeServiceRequestHolder(byte[] body, byte serializerType) {
        ProtocolHeader header = ProtocolHeaderFactory.newSubscribeServiceRequestHeader(body.length, serializerType);
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create SubscribeService Response MessageHolder.
     */
    public static MessageHolder newSubscribeServiceResponseHolder(byte[] body, byte serializerType, int resultCode) {
        ProtocolHeader header = ProtocolHeaderFactory.newSubscribeServiceResponseHeader(body.length, serializerType, resultCode);
        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }
}
