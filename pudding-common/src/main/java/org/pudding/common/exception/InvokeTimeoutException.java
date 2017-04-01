package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class InvokeTimeoutException extends RuntimeException {

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
