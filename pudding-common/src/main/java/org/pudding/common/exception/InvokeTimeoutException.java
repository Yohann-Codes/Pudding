package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class ServiceStartFailedException extends Exception {

    public ServiceStartFailedException() {
        super();
    }

    public ServiceStartFailedException(String messge) {
        super(messge);
    }

    public ServiceStartFailedException(Throwable cause) {
        super(cause);
    }

    public ServiceStartFailedException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
