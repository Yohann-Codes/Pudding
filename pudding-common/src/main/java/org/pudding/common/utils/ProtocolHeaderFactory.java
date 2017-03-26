package org.pudding.common.utils;

import org.pudding.common.ProtocolHeader;
import org.pudding.common.config.PuddingConfig;

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
        header.setType(ProtocolHeader.type(PuddingConfig.serializerType, ProtocolHeader.HEATBEAT));
        header.setSign((byte) 0);
        header.setId(0);
        header.setErrorCode(0);
        header.setBodyLength(0);
        return header;
    }
}
