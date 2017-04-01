package org.pudding.rpc.consumer.proxy;

/**
 * @author Yohann.
 */
public interface InvokeHandler {
    /**
     * 远程调用返回后会回调此方法.
     *
     * @param invokeId
     * @param result
     */
    void invokeComplete(Long invokeId, Object result, int resultCode);
}
