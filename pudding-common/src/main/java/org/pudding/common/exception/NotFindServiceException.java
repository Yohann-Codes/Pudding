package org.pudding.common.exception;

/**
 * Not find service.
 *
 * @author Yohann.
 */
public class NotFindServiceException extends RuntimeException {

    public NotFindServiceException() {
        super();
    }

    public NotFindServiceException(String messge) {
        super(messge);
    }

    public NotFindServiceException(Throwable cause) {
        super(cause);
    }

    public NotFindServiceException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
