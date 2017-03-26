package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class RepeatConnectRegistryException extends RuntimeException {

    public RepeatConnectRegistryException() {
        super();
    }

    public RepeatConnectRegistryException(String messge) {
        super(messge);
    }

    public RepeatConnectRegistryException(Throwable cause) {
        super(cause);
    }

    public RepeatConnectRegistryException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
