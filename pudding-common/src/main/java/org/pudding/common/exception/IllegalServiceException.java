package org.pudding.common.exception;

/**
 * Services do not conform to the requirements.
 *
 * 1). The service can only implement an interface.
 * 2). The service interface must extend {@link java.io.Serializable}.
 *
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
