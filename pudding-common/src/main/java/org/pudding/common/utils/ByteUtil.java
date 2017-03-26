package org.pudding.common.utils;

/**
 * @author Yohann.
 */
public class ByteUtil {
    public static byte high4(byte code) {
        return (byte) ((code & 0xff) >> 4);
    }

    public static byte low4(byte code) {
        return (byte) (code & 0x0f);
    }

    public static byte code(byte high4, byte low4) {
        return (byte) ((high4 << 4) + low4);
    }
}
