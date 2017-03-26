package org.pudding.common.utils;

import org.pudding.common.exception.IllegalAddrFormatException;

/**
 * 自定义地址解析工具(host:port).
 *
 * @author Yohann.
 */
public class AddressUtil {

    /**
     * 检查地址格式.
     */
    public static void checkFormat(String address) {
        if (address == null) {
            throw new NullPointerException("address == null");
        }
        String[] s = address.split(":");
        if (s.length != 2) {
            throw new IllegalAddrFormatException();
        }
    }

    /**
     * @return String类型主机IP.
     */
    public static String host(String address) {
        String[] s = address.split(":");
        return s[0];
    }

    /**
     * @return int类型端口号.
     */
    public static int port(String address) {
        String[] s = address.split(":");
        return Integer.parseInt(s[1]);
    }
}
