package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class InvokeFailedException extends RuntimeException {

    public InvokeFailedException() {
        super();
    }

    public InvokeFailedException(String messge) {
        super(messge);
    }

    public InvokeFailedException(Throwable cause) {
        super(cause);
    }

    public InvokeFailedException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
