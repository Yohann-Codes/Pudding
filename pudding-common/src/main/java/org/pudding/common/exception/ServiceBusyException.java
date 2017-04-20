package org.pudding.common.exception;

/**
 * Invocation timeout.
 *
 * @author Yohann.
 */
public class InvokeTimeoutException extends Exception {

    public InvokeTimeoutException() {
        super();
    }

    public InvokeTimeoutException(String messge) {
        super(messge);
    }

    public InvokeTimeoutException(Throwable cause) {
        super(cause);
    }

    public InvokeTimeoutException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
