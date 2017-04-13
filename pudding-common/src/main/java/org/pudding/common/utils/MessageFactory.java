package org.pudding.common.utils;

import org.pudding.common.protocol.Message;
import org.pudding.common.protocol.ProtocolHeader;

/**
 * Message factory.
 *
 * @author Yohann.
 */
public class MessageFactory {

    /**
     * Create heartbeat message.
     */
    public static Message newHeartbeatMessage() {
        ProtocolHeader header = ProtocolHeaderFactory.newHeartbeatHeader();
        Message message = new Message();
        message.setHeader(header);
        message.setBody(new byte[]{});
        return message;
    }

    /**
     * Create Request Message of publishing service.
     */
    public static Message newPublishRequestMessage(byte[] body, byte serializerType) {
        ProtocolHeader header = ProtocolHeaderFactory.newPublishRequestHeader(body.length, serializerType);
        Message holder = new Message();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create PublishService Response MessageHolder.
     */
    public static Message newPublishServiceResponseHolder(byte[] body, byte serializerType, int resultCode) {
        ProtocolHeader header = ProtocolHeaderFactory.newPublishResponseHeader(body.length, serializerType, resultCode);
        Message holder = new Message();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create SubscribeService Request MessageHolder.
     */
    public static Message newSubscribeServiceRequestHolder(byte[] body, byte serializerType) {
        ProtocolHeader header = ProtocolHeaderFactory.newSubscribeRequestHeader(body.length, serializerType);
        Message holder = new Message();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create SubscribeService Response MessageHolder.
     */
    public static Message newSubscribeServiceResponseHolder(byte[] body, byte serializerType, int resultCode) {
        ProtocolHeader header = ProtocolHeaderFactory.newSubscribeResponseHeader(body.length, serializerType, resultCode);
        Message holder = new Message();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create Invoke Request MessageHolder.
     */
    public static Message newInvokeRequestHolder(byte[] body, byte serializerType, long invokeId) {
        ProtocolHeader header = ProtocolHeaderFactory.newInvokeRequestHeader(body.length, serializerType, invokeId);
        Message holder = new Message();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }

    /**
     * Create Invoke Response MessageHolder.
     */
    public static Message newInvokeResponseHolder(byte[] body, byte serializerType, long invokeId, int resultCode) {
        ProtocolHeader header = ProtocolHeaderFactory.newInvokeResponseHeader(body.length, serializerType, invokeId, resultCode);
        Message holder = new Message();
        holder.setHeader(header);
        holder.setBody(body);
        return holder;
    }
}
