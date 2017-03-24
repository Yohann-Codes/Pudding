package org.pudding.serialization.api;

/**
 * 序列化/反序列化方式.
 *
 * @author Yohann.
 */
public enum SerializerType {
    JAVA          ((byte) 0x01),
    KRYO          ((byte) 0x02),
    HESSIAN       ((byte) 0x03),
    GSON          ((byte) 0x04);

    SerializerType(byte value) {
        if (value < 0x01 || value > 0x0f) {
            throw new IllegalArgumentException("out of range(0x01 ~ 0x0f): " + value);
        }
        this.value = value;
    }

    private final byte value;

    public byte value() {
        return value;
    }
}
