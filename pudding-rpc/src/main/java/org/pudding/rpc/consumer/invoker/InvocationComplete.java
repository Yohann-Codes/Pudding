package org.pudding.rpc.consumer.invoker;

import org.pudding.common.model.ResultMeta;

/**
 * @author Yohann.
 */
public interface InvocationComplete {

    /**
     * Imply the invocation has completed.
     */
    void completeInvocation(long sequence, ResultMeta resultMeta, int status);
}
