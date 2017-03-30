package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class ServicePublishFailedException extends Exception {

    public ServicePublishFailedException() {
        super();
    }

    public ServicePublishFailedException(String messge) {
        super(messge);
    }

    public ServicePublishFailedException(Throwable cause) {
        super(cause);
    }

    public ServicePublishFailedException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
