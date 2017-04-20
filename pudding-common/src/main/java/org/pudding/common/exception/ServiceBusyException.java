package org.pudding.common.exception;

/**
 * Service busy.
 *
 * @author Yohann.
 */
public class ServiceBusyException extends RuntimeException {

    public ServiceBusyException() {
        super();
    }

    public ServiceBusyException(String messge) {
        super(messge);
    }

    public ServiceBusyException(Throwable cause) {
        super(cause);
    }

    public ServiceBusyException(String messge, Throwable cause) {
        super(messge, cause);
    }
}
