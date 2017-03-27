package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class ProcessorIsNullException extends Exception {

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
