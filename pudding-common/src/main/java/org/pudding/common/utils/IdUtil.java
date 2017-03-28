package org.pudding.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 为注册和订阅的服务生成唯一的Id.
 *
 * @author Yohann.
 */
public class ServiceIdUtil {
    private static volatile AtomicLong serviceId = new AtomicLong(1);

    public static long generate() {
        if (Long.MAX_VALUE - serviceId.get() < 1) {
            // 重新从0开始生成
            serviceId.set(1);
        }
        return serviceId.getAndIncrement();
    }
}
