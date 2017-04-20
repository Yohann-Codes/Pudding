package org.pudding.transport.protocol;

import org.pudding.common.utils.ByteUtil;

/**
 * Pudding protocol header.
 *
 * @author Yohann.
 */
public final class ProtocolHeader {

    /** header length */
    public static final int HEAD_LENGTH = 20;
    /** Magic */
    public static final short MAGIC = (short) 0xbabe;

    /** Serialization Type, high 4 bits */
    public static final byte JAVA = 0x01; // Java
    public static final byte KRYO = 0x02; // Kryo
    public static final byte HESSIAN = 0x03; // Hessian
    public static final byte GSON = 0x04; // Gson

    /** Message Type, low 4 bits */
    public static final byte REQUEST = 0x01; // request
    public static final byte CLUSTER_SYNC = 0x03; // cluster sync
    public static final byte RESPONSE = 0x02; // response
    public static final byte HEATBEAT = 0x04; // heatbeat
    public static final byte ACK = 0x05; // ack

    /** Sign */
    public static final byte PUBLISH_SERVICE = 0x01; // publish service
    public static final byte UNPUBLISH_SERVICE = 0x02; // unpublish service
    public static final byte SUBSCRIBE_SERVICE = 0x03; // subscribe service
    public static final byte DISPATCH_SERVICE= 0x04; // dispatch service
    public static final byte OFFLINE_SERVICE = 0x05; // notice for unpublishing
    public static final byte INVOKE_SERVICE = 0x06; // invoke remote

    /** Status */
    public static final int SUCCESS = 200; // success
    public static final int NOT_FIND_SERVICE = 501; // not find service
    public static final int SERVER_BUSY = 502; // server busy

    private short magic;
    private byte type; // high 4 bits: serializerType; low 4 bits: messageType
    private byte sign;
    private long sequence; // <sequence, request, ack>
    private int status; // response status
    private int bodyLength; // body length

    public static byte serializationType(byte type) {
        return ByteUtil.high4(type);
    }

    public static byte messageType(byte type) {
        return ByteUtil.low4(type);
    }

    public static byte type(byte serializationType, byte messageType) {
        return ByteUtil.code(serializationType, messageType);
    }

    public short getMagic() {
        return magic;
    }

    public ProtocolHeader setMagic(short magic) {
        this.magic = magic;
        return this;
    }

    public byte getType() {
        return type;
    }

    public ProtocolHeader setType(byte type) {
        this.type = type;
        return this;
    }

    public byte getSign() {
        return sign;
    }

    public ProtocolHeader setSign(byte sign) {
        this.sign = sign;
        return this;
    }

    public long getSequence() {
        return sequence;
    }

    public ProtocolHeader setSequence(long sequence) {
        this.sequence = sequence;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ProtocolHeader setStatus(int status) {
        this.status = status;
        return this;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public ProtocolHeader setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
        return this;
    }

    @Override
    public String toString() {
        return "ProtocolHeader{" +
                "magic=" + magic +
                ", type=" + type +
                ", sign=" + sign +
                ", sequence=" + sequence +
                ", status=" + status +
                ", bodyLength=" + bodyLength +
                '}';
    }
}
