package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class ServiceSubscribeFailedException extends Exception {

    public ServiceSubscribeFailedException() {
        super();
    }

    public ServiceSubscribeFailedException(String messge) {
        super(messge);
    }

    public ServiceSubscribeFailedException(Throwable cause) {
        super(cause);
    }

    public ServiceSubscribeFailedException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
