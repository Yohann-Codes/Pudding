package org.pudding.common.exception;

/**
 * @author Yohann.
 */
public class IllegalAddrFormatException extends Exception {

    public IllegalAddrFormatException() {
        super();
    }

    public IllegalAddrFormatException(String messge) {
        super(messge);
    }

    public IllegalAddrFormatException(Throwable cause) {
        super(cause);
    }

    public IllegalAddrFormatException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
