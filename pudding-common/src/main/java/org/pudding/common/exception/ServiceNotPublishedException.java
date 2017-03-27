package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class ServiceNotPublishedException extends Exception {

    public ServiceNotPublishedException() {
        super();
    }

    public ServiceNotPublishedException(String messge) {
        super(messge);
    }

    public ServiceNotPublishedException(Throwable cause) {
        super(cause);
    }

    public ServiceNotPublishedException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
