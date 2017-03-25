package org.pudding.transport.api;

/**
 * Pudding Future.
 *
 * @author Yohann.
 */
public interface Future {

    /**
     * @return Pudding Channel.
     */
    Channel channel();

    /**
     * 注册监听.
     */
    void addListener(FutureListener listener);
}
