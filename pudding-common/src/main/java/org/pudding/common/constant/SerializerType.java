package org.pudding.common.constant;

/**
 * 序列化/反序列化类型.
 *
 * @author Yohann.
 */
public class SerializerType {

    /** Java */
    public static final byte JAVA = 0x01;

    /** Kryo */
    public static final byte KRYO = 0x02;

    /** Hessian */
    public static final byte HESSIAN = 0x03;

    /** Gson */
    public static final byte GSON = 0x04;
}
