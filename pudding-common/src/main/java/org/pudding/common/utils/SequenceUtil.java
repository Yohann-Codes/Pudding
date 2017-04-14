package org.pudding.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Id .
 *
 * @author Yohann.
 */
public class IdUtil {
    private static Object invokeIdLock = new Object();

    /** 远程调用Id */
    private static AtomicLong invokeId = new AtomicLong(1);

    public static long invokeId() {
        synchronized (invokeIdLock) {
            if (Long.MAX_VALUE - invokeId.get() < 1) {
                // 重新从0开始生成
                invokeId.set(1);
            }
            return invokeId.getAndIncrement();
        }
    }
}
