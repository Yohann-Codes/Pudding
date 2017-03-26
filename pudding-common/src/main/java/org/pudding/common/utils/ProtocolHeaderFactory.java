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
        header.setId(0);
        header.setErrorCode(0);
        header.setBodyLength(0);
        return header;
    }
}
