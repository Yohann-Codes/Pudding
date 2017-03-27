package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class NotConnectRegistryException extends Exception {

    public NotConnectRegistryException() {
        super();
    }

    public NotConnectRegistryException(String messge) {
        super(messge);
    }

    public NotConnectRegistryException(Throwable cause) {
        super(cause);
    }

    public NotConnectRegistryException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
