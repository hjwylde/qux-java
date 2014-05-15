package com.hjwylde.common.error;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public class IllegalTargetNameException extends RuntimeException {

    public IllegalTargetNameException() {}

    public IllegalTargetNameException(String message) {
        super(message);
    }

    public IllegalTargetNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTargetNameException(Throwable cause) {
        super(cause);
    }
}
