package org.pudding.common.utils;

import org.pudding.common.config.PuddingConfig;
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
     * Create PublishService ProtocolHeader.
     */
    public static ProtocolHeader newPublishServiceHeader(int bodyLength) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(PuddingConfig.serializerType, ProtocolHeader.MESSAGE));
        header.setSign(ProtocolHeader.PUBLISH_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(0);
        header.setBodyLength(bodyLength);
        return header;
    }

    /**
     * Create UnpublishService ProtocolHeader.
     */
    public static ProtocolHeader newUnpublishServiceHeader(int bodyLength) {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(ProtocolHeader.type(PuddingConfig.serializerType, ProtocolHeader.MESSAGE));
        header.setSign(ProtocolHeader.UNPUBLISH_SERVICE);
        header.setInvokeId(0);
        header.setResultCode(0);
        header.setBodyLength(bodyLength);
        return header;
    }
}
