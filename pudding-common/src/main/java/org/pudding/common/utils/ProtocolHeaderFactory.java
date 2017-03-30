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
    public static ProtocolHeader newPublishServiceRequestHeader(int bodyLength, byte serializerType) {
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
    public static ProtocolHeader newPublishServiceResponseHeader(int bodyLength, byte serializerType, int resultCode) {
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
    public static ProtocolHeader newSubscribeServiceRequestHeader(int bodyLength, byte serializerType) {
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
    public static ProtocolHeader newSubscribeServiceResponseHeader(int bodyLength, byte serializerType, int resultCode) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(serializerType, ProtocolHeader.RESPONSE));
        header.setSign(ProtocolHeader.SUBSCRIBE_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(resultCode);
        header.setBodyLength(bodyLength);
        return header;
    }
}
