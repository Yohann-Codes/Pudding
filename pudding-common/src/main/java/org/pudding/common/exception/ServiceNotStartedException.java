package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class ServiceNotStartedException extends RuntimeException {

    public ServiceNotStartedException() {
        super();
    }

    public ServiceNotStartedException(String messge) {
        super(messge);
    }

    public ServiceNotStartedException(Throwable cause) {
        super(cause);
    }

    public ServiceNotStartedException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
