package org.pudding.transport.api;

/**
 * Pudding FutureListener.
 *
 * @author Yohann.
 */
public interface FutureListener {
    void operationComplete(boolean isSuccess);
}
