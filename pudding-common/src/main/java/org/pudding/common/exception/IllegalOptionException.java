package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class IllegalOptionException extends Exception {

    public IllegalOptionException() {
        super();
    }

    public IllegalOptionException(String messge) {
        super(messge);
    }

    public IllegalOptionException(Throwable cause) {
        super(cause);
    }

    public IllegalOptionException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
