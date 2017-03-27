package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class IllegalServiceException extends RuntimeException {

    public IllegalServiceException() {
        super();
    }

    public IllegalServiceException(String messge) {
        super(messge);
    }

    public IllegalServiceException(Throwable cause) {
        super(cause);
    }

    public IllegalServiceException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
