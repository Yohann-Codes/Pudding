package org.pudding.rpc.consumer.future;

import org.pudding.rpc.utils.InvokeMap;

/**
 * 添加异步调用监听器.
 *
 * @author Yohann.
 */
public class InvokeFuture {

    // 最后一次异步调用的invokeId
    protected static volatile long invokeId;

    /** 保存调用Id和监听器 */
    protected static volatile InvokeMap INVOKE_MAP;

    static {
        INVOKE_MAP = new InvokeMap();
    }

    /**
     * 只能给最后一次调用方法添加监听器.
     *
     * @param listener
     */
    public static <T> void addInvokeFutureListener(InvokeFutureListener<T> listener) {
        INVOKE_MAP.put(invokeId, listener);
    }
}
