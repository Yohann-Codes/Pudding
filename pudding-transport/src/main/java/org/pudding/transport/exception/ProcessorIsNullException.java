package org.pudding.transport.exception;

/**
 * @author Yohann.
 */
public class ProcessorIsNullException extends RuntimeException {

    public ProcessorIsNullException() {
        super();
    }

    public ProcessorIsNullException(String messge) {
        super(messge);
    }

    public ProcessorIsNullException(Throwable cause) {
        super(cause);
    }

    public ProcessorIsNullException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
