package org.pudding.rpc.consumer.future;

/**
 * @author Yohann.
 */
public interface InvokeFutureListener<T> {

    /**
     * 调用成功
     *
     * @param result 返回值
     */
    void success(T result);

    /**
     * 调用失败.
     *
     * @param e
     */
    void failure(Exception e);
}
