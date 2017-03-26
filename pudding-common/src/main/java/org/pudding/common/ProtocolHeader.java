package org.pudding.common;

import org.pudding.common.utils.ByteUtil;

/**
 * Pudding传输层协议头.
 *
 * @author Yohann.
 */
public class ProtocolHeader {

    /** 协议头长度 */
    public static final int HEAD_LENGTH = 20;
    /** Magic */
    public static final short MAGIC = (short) 0xbabe;

    /** Serialization Type, type的高地址4位 */
    public static final byte JAVA = 0x01; // Java
    public static final byte KRYO = 0x02; // Kryo
    public static final byte HESSIAN = 0x03; // Hessian
    public static final byte GSON = 0x04; // Gson


    /** DataPacket Type, type的低地址4位 */
    public static final byte MESSAGE = 0x01; // 消息
    public static final byte HEATBEAT = 0x02; // 心跳
    public static final byte ACK = 0x03; // 消息确认应答

    /** Sign 消息标识 */
    public static final byte PUBLISH_SERVICE = 0x01; // 发布服务
    public static final byte UNPUBLISH_SERVICE = 0x02; // 取消发布
    public static final byte SUBSCRIBE_SERVICE = 0x03; // 订阅服务
    public static final byte DISPATCH_SERVICE= 0x04; // 分派服务
    public static final byte OFFLINE_SERVICE = 0x05; // 服务下线通知
    public static final byte INVOKE_SERVICE = 0x06; // 服务调用
    public static final byte RETURN_SERVICE = 0x07; // 调用返回
    public static final byte ERROR = 0x08; // 错误

    /** errorCode, Sign不是ERROR时, errorCode = 0 */
    //...

    private short magic;
    private byte type; // 高4位: serializerType, 低4位: dataPacketType
    private byte sign;
    private long id; // MESSAGE, ACK
    private int errorCode; // 错误码
    private int bodyLength; // 消息体长度

    public static byte serializationCode(byte code) {
        return ByteUtil.high4(code);
    }

    public static byte dataPacketCode(byte code) {
        return ByteUtil.low4(code);
    }

    public static byte type(byte serializationCode, byte dataPacketCode) {
        return ByteUtil.code(serializationCode, dataPacketCode);
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    @Override
    public String toString() {
        return "ProtocolHeader{" +
                "magic=" + magic +
                ", type=" + type +
                ", sign=" + sign +
                ", id=" + id +
                ", errorCode=" + errorCode +
                ", bodyLength=" + bodyLength +
                '}';
    }
}
