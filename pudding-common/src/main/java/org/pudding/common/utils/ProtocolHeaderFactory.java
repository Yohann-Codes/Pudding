package org.pudding.common.utils;

import org.pudding.common.protocol.ProtocolHeader;

/**
 * ProtocolHeader Factory.
 *
 * @author Yohann.
 */
public class ProtocolHeaderFactory {

    /**
     * Create Heartbeat ProtocolHeader.
     */
    public static ProtocolHeader newHeartbeatHeader() {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        // 心跳包可忽略高地址的4位序列化/反序列化标志
        header.setType(ProtocolHeader.type((byte) 0, ProtocolHeader.HEATBEAT));
        header.setSign((byte) 0);
        header.setInvokeId(0);
        header.setResultCode(0);
        header.setBodyLength(0);
        return header;
    }

    /**
     * Create PublishService Request ProtocolHeader.
     */
    public static ProtocolHeader newPublishRequestHeader(int bodyLength, byte serializerType) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.REQUEST));
        header.setSign(ProtocolHeader.PUBLISH_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(0);
        header.setBodyLength(bodyLength);
        return header;
    }

    /**
     * Create PublishService Response ProtocolHeader.
     */
    public static ProtocolHeader newPublishResponseHeader(int bodyLength, byte serializerType, int resultCode) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.RESPONSE));
        header.setSign(ProtocolHeader.PUBLISH_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(resultCode);
        header.setBodyLength(bodyLength);
        return header;
    }

    /**
     * Create SubscribeService Request ProtocolHeader.
     */
    public static ProtocolHeader newSubscribeRequestHeader(int bodyLength, byte serializerType) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.REQUEST));
        header.setSign(ProtocolHeader.SUBSCRIBE_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(0);
        header.setBodyLength(bodyLength);
        return header;
    }

    /**
     * Create SubscribeService Response ProtocolHeader.
     */
    public static ProtocolHeader newSubscribeResponseHeader(int bodyLength, byte serializerType, int resultCode) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.RESPONSE));
        header.setSign(ProtocolHeader.SUBSCRIBE_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(resultCode);
        header.setBodyLength(bodyLength);
        return header;
    }

    /**
     * Create Invoke Request ProtocolHeader.
     */
    public static ProtocolHeader newInvokeRequestHeader(int bodyLength, byte serializerType, long invokeId) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.REQUEST));
        header.setSign(ProtocolHeader.INVOKE_SERVICE);
        header.setInvokeId(invokeId);
        header.setResultCode(0);
        header.setBodyLength(bodyLength);
        return header;
    }

    /**
     * Create Invoke Response ProtocolHeader.
     */
    public static ProtocolHeader newInvokeResponseHeader(int bodyLength, byte serializerType, long invokeId, int resultCode) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.RESPONSE));
        header.setSign(ProtocolHeader.INVOKE_SERVICE);
        header.setInvokeId(invokeId);
        header.setResultCode(resultCode);
        header.setBodyLength(bodyLength);
        return header;
    }
}
